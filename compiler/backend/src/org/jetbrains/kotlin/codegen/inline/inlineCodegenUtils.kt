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

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.kotlin.codegen.state.JetTypeMapper
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.load.kotlin.FileBasedKotlinClass
import org.jetbrains.kotlin.load.kotlin.KotlinJvmBinaryPackageSourceElement
import org.jetbrains.kotlin.load.kotlin.KotlinJvmBinarySourceElement
import org.jetbrains.kotlin.load.kotlin.VirtualFileKotlinClass
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader
import org.jetbrains.kotlin.load.kotlin.incremental.components.IncrementalCache
import org.jetbrains.kotlin.resolve.source.PsiSourceElement
import org.jetbrains.kotlin.serialization.Flags
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.deserialization.NameResolver
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedCallableMemberDescriptor
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedSimpleFunctionDescriptor
import org.jetbrains.kotlin.serialization.jvm.BitEncoding
import org.jetbrains.kotlin.serialization.jvm.JvmProtoBufUtil

public val FunctionDescriptor.sourceFilePath: String
    get() {
        val source = source as PsiSourceElement
        val containingFile = source.psi?.containingFile
        return containingFile?.virtualFile?.canonicalPath!!
    }

public fun FunctionDescriptor.getClassFilePath(typeMapper: JetTypeMapper, cache: IncrementalCache): String {
    val container = containingDeclaration as? DeclarationDescriptorWithSource
    val source = container?.source

    return when (source) {
        is KotlinJvmBinaryPackageSourceElement -> {
            if (this !is DeserializedCallableMemberDescriptor) {
                throw AssertionError("Expected DeserializedCallableMemberDescriptor, got: $this")
            }
            val kotlinClass = source.getContainingBinaryClass(this) ?:
                    throw AssertionError("Descriptor $this is not found, in: $source")
            if (kotlinClass !is VirtualFileKotlinClass) {
                throw AssertionError("Expected VirtualFileKotlinClass, got $kotlinClass")
            }
            kotlinClass.file.canonicalPath!!
        }
        is KotlinJvmBinarySourceElement -> {
            assert(this is DeserializedSimpleFunctionDescriptor) { "Expected DeserializedSimpleFunctionDescriptor, got: $this" }
            val kotlinClass = source.binaryClass as VirtualFileKotlinClass
            kotlinClass.file.canonicalPath!!
        }
        else -> {
            val implementationOwnerType = typeMapper.mapImplementationOwner(this)
            val className = implementationOwnerType.internalName
            cache.getClassFilePath(className)
        }
    }
}

public fun inlineFunctionsJvmNames(bytes: ByteArray): Set<String> {
    val header = readKotlinHeader(bytes)
    val annotationData = header.annotationData
    val strings = header.strings

    if (annotationData == null || strings == null) return emptySet()

    return when (header.kind) {
        KotlinClassHeader.Kind.CLASS -> {
            val classData = JvmProtoBufUtil.readClassDataFrom(BitEncoding.decodeBytes(annotationData), strings)
            inlineFunctionsJvmNames(classData.classProto.functionList, classData.nameResolver, classData.classProto.typeTable)
        }
        KotlinClassHeader.Kind.FILE_FACADE,
        KotlinClassHeader.Kind.MULTIFILE_CLASS,
        KotlinClassHeader.Kind.MULTIFILE_CLASS_PART -> {
            val packageData = JvmProtoBufUtil.readPackageDataFrom(BitEncoding.decodeBytes(annotationData), strings)
            inlineFunctionsJvmNames(packageData.packageProto.functionList, packageData.nameResolver, packageData.packageProto.typeTable)
        }
        else -> emptySet<String>()
    }
}

private fun inlineFunctionsJvmNames(functions: List<ProtoBuf.Function>, nameResolver: NameResolver, protoTypeTable: ProtoBuf.TypeTable): Set<String> {
    val typeTable = TypeTable(protoTypeTable)
    val inlineFunctions = functions.filter { Flags.IS_INLINE.get(it.flags) }
    val jvmNames = inlineFunctions.map {
        JvmProtoBufUtil.getJvmMethodSignature(it, nameResolver, typeTable)
    }
    return jvmNames.filterNotNull().toSet()
}

private fun readKotlinHeader(bytes: ByteArray): KotlinClassHeader {
    var header: KotlinClassHeader? = null

    FileBasedKotlinClass.create(bytes) { className, classHeader, innerClasses ->
        header = classHeader
        null
    }

    if (header == null) throw AssertionError("Could not read kotlin header from byte array")

    return header!!
}