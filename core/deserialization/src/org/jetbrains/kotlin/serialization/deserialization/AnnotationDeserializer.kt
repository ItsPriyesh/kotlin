/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptorImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.AnnotationValue
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.constants.ConstantValueFactory
import org.jetbrains.kotlin.serialization.ProtoBuf.Annotation
import org.jetbrains.kotlin.serialization.ProtoBuf.Annotation.Argument
import org.jetbrains.kotlin.serialization.ProtoBuf.Annotation.Argument.Value
import org.jetbrains.kotlin.serialization.ProtoBuf.Annotation.Argument.Value.Type
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

public class AnnotationDeserializer(private val module: ModuleDescriptor) {
    private val builtIns: KotlinBuiltIns
        get() = module.builtIns

    private val factory = ConstantValueFactory(builtIns)

    public fun deserializeAnnotation(proto: Annotation, nameResolver: NameResolver): AnnotationDescriptor {
        val annotationClass = resolveClass(nameResolver.getClassId(proto.getId()))

        val arguments = if (proto.getArgumentCount() == 0 || ErrorUtils.isError(annotationClass)) {
            mapOf()
        }
        else {
            val parameterByName = annotationClass.getConstructors().single().getValueParameters().toMap { it.getName() }
            val arguments = proto.getArgumentList().map { resolveArgument(it, parameterByName, nameResolver) }.filterNotNull()
            arguments.toMap()
        }

        return AnnotationDescriptorImpl(annotationClass.getDefaultType(), arguments, SourceElement.NO_SOURCE)
    }

    private fun resolveArgument(
            proto: Argument,
            parameterByName: Map<Name, ValueParameterDescriptor>,
            nameResolver: NameResolver
    ): Pair<ValueParameterDescriptor, ConstantValue<*>>? {
        val parameter = parameterByName[nameResolver.getName(proto.getNameId())] ?: return null
        return Pair(parameter, resolveValue(parameter.getType(), proto.getValue(), nameResolver))
    }

    public fun resolveValue(
            expectedType: KotlinType,
            value: Value,
            nameResolver: NameResolver
    ): ConstantValue<*> {
        val result: ConstantValue<*> = when (value.getType()) {
            Type.BYTE -> factory.createByteValue(value.getIntValue().toByte())
            Type.CHAR -> factory.createCharValue(value.getIntValue().toChar())
            Type.SHORT -> factory.createShortValue(value.getIntValue().toShort())
            Type.INT -> factory.createIntValue(value.getIntValue().toInt())
            Type.LONG -> factory.createLongValue(value.getIntValue())
            Type.FLOAT -> factory.createFloatValue(value.getFloatValue())
            Type.DOUBLE -> factory.createDoubleValue(value.getDoubleValue())
            Type.BOOLEAN -> factory.createBooleanValue(value.getIntValue() != 0L)
            Type.STRING -> {
                factory.createStringValue(nameResolver.getString(value.getStringValue()))
            }
            Type.CLASS -> {
                // TODO: support class literals
                error("Class literal annotation arguments are not supported yet (${nameResolver.getClassId(value.getClassId())})")
            }
            Type.ENUM -> {
                resolveEnumValue(nameResolver.getClassId(value.getClassId()), nameResolver.getName(value.getEnumValueId()))
            }
            Type.ANNOTATION -> {
                AnnotationValue(deserializeAnnotation(value.getAnnotation(), nameResolver))
            }
            Type.ARRAY -> {
                val expectedIsArray = KotlinBuiltIns.isArray(expectedType) || KotlinBuiltIns.isPrimitiveArray(expectedType)
                val arrayElements = value.getArrayElementList()

                val actualArrayType =
                        if (arrayElements.isNotEmpty()) {
                            val actualElementType = resolveArrayElementType(arrayElements.first(), nameResolver)
                            builtIns.getPrimitiveArrayJetTypeByPrimitiveJetType(actualElementType) ?:
                            builtIns.getArrayType(Variance.INVARIANT, actualElementType)
                        }
                        else {
                            // In the case of empty array, no element has the element type, so we fall back to the expected type, if any.
                            // This is not very accurate when annotation class has been changed without recompiling clients,
                            // but should not in fact matter because the value is empty anyway
                            if (expectedIsArray) expectedType else builtIns.getArrayType(Variance.INVARIANT, builtIns.getAnyType())
                        }

                val expectedElementType = builtIns.getArrayElementType(if (expectedIsArray) expectedType else actualArrayType)

                factory.createArrayValue(
                        arrayElements.map {
                            resolveValue(expectedElementType, it, nameResolver)
                        },
                        actualArrayType
                )
            }
            else -> error("Unsupported annotation argument type: ${value.getType()} (expected $expectedType)")
        }

        if (result.type isSubtypeOf expectedType) {
            return result
        }
        else {
            // This means that an annotation class has been changed incompatibly without recompiling clients
            return factory.createErrorValue("Unexpected argument value")
        }
    }

    // NOTE: see analogous code in BinaryClassAnnotationAndConstantLoaderImpl
    private fun resolveEnumValue(enumClassId: ClassId, enumEntryName: Name): ConstantValue<*> {
        val enumClass = resolveClass(enumClassId)
        if (enumClass.getKind() == ClassKind.ENUM_CLASS) {
            val enumEntry = enumClass.getUnsubstitutedInnerClassesScope().getClassifier(enumEntryName)
            if (enumEntry is ClassDescriptor) {
                return factory.createEnumValue(enumEntry)
            }
        }
        return factory.createErrorValue("Unresolved enum entry: $enumClassId.$enumEntryName")
    }

    private fun resolveArrayElementType(value: Value, nameResolver: NameResolver): KotlinType =
            with(builtIns) {
                when (value.getType()) {
                    Type.BYTE -> getByteType()
                    Type.CHAR -> getCharType()
                    Type.SHORT -> getShortType()
                    Type.INT -> getIntType()
                    Type.LONG -> getLongType()
                    Type.FLOAT -> getFloatType()
                    Type.DOUBLE -> getDoubleType()
                    Type.BOOLEAN -> getBooleanType()
                    Type.STRING -> getStringType()
                    Type.CLASS -> error("Arrays of class literals are not supported yet") // TODO: support arrays of class literals
                    Type.ENUM -> resolveClass(nameResolver.getClassId(value.getClassId())).getDefaultType()
                    Type.ANNOTATION -> resolveClass(nameResolver.getClassId(value.getAnnotation().getId())).getDefaultType()
                    Type.ARRAY -> error("Array of arrays is impossible")
                    else -> error("Unknown type: ${value.getType()}")
                }
            }

    private fun resolveClass(classId: ClassId): ClassDescriptor {
        return module.findClassAcrossModuleDependencies(classId)
               ?: ErrorUtils.createErrorClass(classId.asSingleFqName().asString())
    }
}
