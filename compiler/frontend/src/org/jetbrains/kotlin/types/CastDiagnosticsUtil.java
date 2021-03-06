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

package org.jetbrains.kotlin.types;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.platform.PlatformToKotlinClassMap;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.ClassKind;
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker;
import org.jetbrains.kotlin.types.checker.TypeCheckingProcedure;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CastDiagnosticsUtil {

    // As this method produces a warning, it must be _complete_ (not sound), i.e. every time it says "cast impossible",
    // it must be really impossible
    public static boolean isCastPossible(
            @NotNull KotlinType lhsType,
            @NotNull KotlinType rhsType,
            @NotNull PlatformToKotlinClassMap platformToKotlinClassMap
    ) {
        if (KotlinBuiltIns.isNullableNothing(lhsType) && !TypeUtils.isNullableType(rhsType)) return false;
        if (isRelated(lhsType, rhsType, platformToKotlinClassMap)) return true;
        // This is an oversimplification (which does not render the method incomplete):
        // we consider any type parameter capable of taking any value, which may be made more precise if we considered bounds
        if (TypeUtils.isTypeParameter(lhsType) || TypeUtils.isTypeParameter(rhsType)) return true;
        if (isFinal(lhsType) || isFinal(rhsType)) return false;
        if (isTrait(lhsType) || isTrait(rhsType)) return true;
        return false;
    }

    /**
     * Two types are related, roughly, when one is a subtype or supertype of the other.
     * <p/>
     * Note that some types have platform-specific counterparts, i.e. kotlin.String is mapped to java.lang.String,
     * such types (and all their sub- and supertypes) are related too.
     * <p/>
     * Due to limitations in PlatformToKotlinClassMap, we only consider mapping of platform classes to Kotlin classed
     * (i.e. java.lang.String -> kotlin.String) and ignore mappings that go the other way.
     */
    private static boolean isRelated(@NotNull KotlinType a, @NotNull KotlinType b, @NotNull PlatformToKotlinClassMap platformToKotlinClassMap) {
        List<KotlinType> aTypes = mapToPlatformIndependentTypes(TypeUtils.makeNotNullable(a), platformToKotlinClassMap);
        List<KotlinType> bTypes = mapToPlatformIndependentTypes(TypeUtils.makeNotNullable(b), platformToKotlinClassMap);

        for (KotlinType aType : aTypes) {
            for (KotlinType bType : bTypes) {
                if (KotlinTypeChecker.DEFAULT.isSubtypeOf(aType, bType)) return true;
                if (KotlinTypeChecker.DEFAULT.isSubtypeOf(bType, aType)) return true;
            }
        }

        return false;
    }

    private static List<KotlinType> mapToPlatformIndependentTypes(
            @NotNull KotlinType type,
            @NotNull PlatformToKotlinClassMap platformToKotlinClassMap
    ) {
        ClassifierDescriptor descriptor = type.getConstructor().getDeclarationDescriptor();
        if (!(descriptor instanceof ClassDescriptor)) return Collections.singletonList(type);

        ClassDescriptor originalClass = (ClassDescriptor) descriptor;
        Collection<ClassDescriptor> kotlinClasses = platformToKotlinClassMap.mapPlatformClass(originalClass);
        if (kotlinClasses.isEmpty()) return Collections.singletonList(type);

        List<KotlinType> result = Lists.newArrayListWithCapacity(2);
        result.add(type);
        for (ClassDescriptor classDescriptor : kotlinClasses) {
            KotlinType kotlinType = TypeUtils.substituteProjectionsForParameters(classDescriptor, type.getArguments());
            result.add(kotlinType);
        }

        return result;
    }

    private static boolean isFinal(@NotNull KotlinType type) {
        return !TypeUtils.canHaveSubtypes(KotlinTypeChecker.DEFAULT, type);
    }

    private static boolean isTrait(@NotNull KotlinType type) {
        ClassifierDescriptor descriptor = type.getConstructor().getDeclarationDescriptor();
        return descriptor instanceof ClassDescriptor && ((ClassDescriptor) descriptor).getKind() == ClassKind.INTERFACE;
    }

    /**
     * Check if cast from supertype to subtype is erased.
     * It is an error in "is" statement and warning in "as".
     */
    public static boolean isCastErased(@NotNull KotlinType supertype, @NotNull KotlinType subtype, @NotNull KotlinTypeChecker typeChecker) {
        // cast between T and T? is always OK
        if (supertype.isMarkedNullable() || subtype.isMarkedNullable()) {
            return isCastErased(TypeUtils.makeNotNullable(supertype), TypeUtils.makeNotNullable(subtype), typeChecker);
        }

        // if it is a upcast, it's never erased
        if (typeChecker.isSubtypeOf(supertype, subtype)) return false;

        // downcasting to a non-reified type parameter is always erased
        if (TypeUtils.isNonReifiedTypeParemeter(subtype)) return true;

        // Check that we are actually casting to a generic type
        // NOTE: this does not account for 'as Array<List<T>>'
        if (allParametersReified(subtype)) return false;

        KotlinType staticallyKnownSubtype = findStaticallyKnownSubtype(supertype, subtype.getConstructor()).getResultingType();

        // If the substitution failed, it means that the result is an impossible type, e.g. something like Out<in Foo>
        // In this case, we can't guarantee anything, so the cast is considered to be erased
        if (staticallyKnownSubtype == null) return true;

        // If the type we calculated is a subtype of the cast target, it's OK to use the cast target instead.
        // If not, it's wrong to use it
        return !typeChecker.isSubtypeOf(staticallyKnownSubtype, subtype);
    }

    /**
     * Remember that we are trying to cast something of type {@code supertype} to {@code subtype}.
     *
     * Since at runtime we can only check the class (type constructor), the rest of the subtype should be known statically, from supertype.
     * This method reconstructs all static information that can be obtained from supertype.
     *
     * Example 1:
     *  supertype = Collection<String>
     *  subtype = List<...>
     *  result = List<String>, all arguments are inferred
     *
     * Example 2:
     *  supertype = Any
     *  subtype = List<...>
     *  result = List<*>, some arguments were not inferred, replaced with '*'
     */
    public static TypeReconstructionResult findStaticallyKnownSubtype(@NotNull KotlinType supertype, @NotNull TypeConstructor subtypeConstructor) {
        assert !supertype.isMarkedNullable() : "This method only makes sense for non-nullable types";

        // Assume we are casting an expression of type Collection<Foo> to List<Bar>
        // First, let's make List<T>, where T is a type variable
        ClassifierDescriptor descriptor = subtypeConstructor.getDeclarationDescriptor();
        assert descriptor != null : "Can't create default type for " + subtypeConstructor;
        KotlinType subtypeWithVariables = descriptor.getDefaultType();

        // Now, let's find a supertype of List<T> that is a Collection of something,
        // in this case it will be Collection<T>
        KotlinType supertypeWithVariables = TypeCheckingProcedure.findCorrespondingSupertype(subtypeWithVariables, supertype);

        final List<TypeParameterDescriptor> variables = subtypeWithVariables.getConstructor().getParameters();

        Map<TypeConstructor, TypeProjection> substitution;
        if (supertypeWithVariables != null) {
            // Now, let's try to unify Collection<T> and Collection<Foo> solution is a map from T to Foo
            TypeUnifier.UnificationResult solution = TypeUnifier.unify(
                    new TypeProjectionImpl(supertype), new TypeProjectionImpl(supertypeWithVariables),
                    new Predicate<TypeConstructor>() {
                        @Override
                        public boolean apply(TypeConstructor typeConstructor) {
                            ClassifierDescriptor descriptor = typeConstructor.getDeclarationDescriptor();
                            return descriptor instanceof TypeParameterDescriptor && variables.contains(descriptor);
                        }
                    });
            substitution = Maps.newHashMap(solution.getSubstitution());
        }
        else {
            // If there's no corresponding supertype, no variables are determined
            // This may be OK, e.g. in case 'Any as List<*>'
            substitution = Maps.newHashMapWithExpectedSize(variables.size());
        }

        // If some of the parameters are not determined by unification, it means that these parameters are lost,
        // let's put stars instead, so that we can only cast to something like List<*>, e.g. (a: Any) as List<*>
        boolean allArgumentsInferred = true;
        for (TypeParameterDescriptor variable : variables) {
            TypeProjection value = substitution.get(variable.getTypeConstructor());
            if (value == null) {
                substitution.put(
                        variable.getTypeConstructor(),
                        TypeUtils.makeStarProjection(variable)
                );
                allArgumentsInferred = false;
            }
        }

        // At this point we have values for all type parameters of List
        // Let's make a type by substituting them: List<T> -> List<Foo>
        KotlinType substituted = TypeSubstitutor.create(substitution).substitute(subtypeWithVariables, Variance.INVARIANT);

        return new TypeReconstructionResult(substituted, allArgumentsInferred);
    }

    private static boolean allParametersReified(KotlinType subtype) {
        for (TypeParameterDescriptor parameterDescriptor : subtype.getConstructor().getParameters()) {
            if (!parameterDescriptor.isReified()) return false;
        }
        return true;
    }

    private CastDiagnosticsUtil() {}
}
