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

package org.jetbrains.kotlin.resolve.jvm.kotlinSignature;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationArgument;
import org.jetbrains.kotlin.load.java.structure.JavaLiteralAnnotationArgument;
import org.jetbrains.kotlin.load.java.structure.JavaMember;

import static org.jetbrains.kotlin.load.java.JvmAnnotationNames.*;

public class SignaturesUtil {
    private SignaturesUtil() {
    }

    @Nullable
    public static String getKotlinSignature(@NotNull JavaMember member) {
        JavaAnnotation newAnnotation = member.findAnnotation(KOTLIN_SIGNATURE);
        if (newAnnotation != null) return extractKotlinSignatureArgument(newAnnotation);

        JavaAnnotation oldAnnotation = member.findAnnotation(OLD_KOTLIN_SIGNATURE);
        if (oldAnnotation != null) return extractKotlinSignatureArgument(oldAnnotation);

        return null;
    }

    @Nullable
    private static String extractKotlinSignatureArgument(@NotNull JavaAnnotation annotation) {
        JavaAnnotationArgument argument = annotation.findArgument(DEFAULT_ANNOTATION_MEMBER_NAME);
        if (argument instanceof JavaLiteralAnnotationArgument) {
            Object value = ((JavaLiteralAnnotationArgument) argument).getValue();
            if (value instanceof String) {
                return StringUtil.unescapeStringCharacters((String) value);
            }
        }
        return null;
    }
}
