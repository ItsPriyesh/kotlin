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

package org.jetbrains.kotlin.load.kotlin

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.JetFile
import org.jetbrains.kotlin.psi.JetNamedFunction
import org.jetbrains.kotlin.psi.JetProperty
import org.jetbrains.kotlin.resolve.jvm.JvmClassName
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedCallableMemberDescriptor
import org.jetbrains.kotlin.serialization.jvm.JvmProtoBuf
import org.jetbrains.org.objectweb.asm.Type
import java.util.Locale
import kotlin.platform.platformStatic

public object PackagePartClassUtils {
    public @platformStatic fun getPathHashCode(file: VirtualFile): Int {
        return file.path.toLowerCase().hashCode()
    }

    private val PART_CLASS_NAME_SUFFIX = "Kt"

    private @platformStatic fun getPartClassName(str: String): String =
            if (str.isEmpty())
                "_$PART_CLASS_NAME_SUFFIX"
            else
                getSanitizedJavaClassName(str) + PART_CLASS_NAME_SUFFIX

    private @platformStatic fun getSanitizedJavaClassName(str: String): String =
            str.replace("[^\\p{L}\\p{Digit}]".toRegex(), "_").capitalizeAsJavaIdentifier()

    private @platformStatic fun String.capitalizeAsJavaIdentifier(): String =
            if (Character.isJavaIdentifierStart(charAt(0)))
                substring(0, 1).toUpperCase(Locale.ENGLISH) + substring(1)
            else
                "_$this"

    @TestOnly
    public @platformStatic fun getDefaultPartFqName(facadeClassFqName: FqName, file: VirtualFile): FqName =
            getDefaultPartFqNameForFilePath(facadeClassFqName.parent(), file.name)

    private @platformStatic fun getDefaultPartFqNameForFilePath(packageFqName: FqName, fileName: String): FqName {
        val nameWithoutExtension = FileUtil.getNameWithoutExtension(fileName)
        return packageFqName.child(Name.identifier(getPartClassName(nameWithoutExtension)))
    }

    public @platformStatic fun isPartClassFqName(classFqName: FqName): Boolean =
            classFqName.shortName().asString().endsWith(PART_CLASS_NAME_SUFFIX)

    public @platformStatic fun getPackagePartType(file: JetFile): Type =
            Type.getObjectType(getPackagePartInternalName(file))

    public @platformStatic fun getPackagePartInternalName(file: JetFile): String {
        val fqName = getPackagePartFqName(file)
        return JvmClassName.byFqNameWithoutInnerClasses(fqName).internalName
    }

    public @platformStatic fun getPackagePartFqName(file: JetFile): FqName =
            getDefaultPartFqNameForFilePath(file.packageFqName, file.name)

    public @platformStatic fun getPackagePartFqName(callable: DeserializedCallableMemberDescriptor): FqName {
        val implClassName = callable.nameResolver.getName(callable.proto.getExtension(JvmProtoBuf.implClassName))
        val packageFqName = (callable.containingDeclaration as PackageFragmentDescriptor).fqName
        return packageFqName.child(implClassName)
    }

    public @platformStatic fun getFilesWithCallables(files: Collection<JetFile>): List<JetFile> =
            files.filter { fileHasTopLevelCallables(it) }

    public @platformStatic fun fileHasTopLevelCallables(file: JetFile): Boolean =
            file.declarations.any { it is JetProperty || it is JetNamedFunction }

    public @platformStatic fun getFilesForPart(partFqName: FqName, files: Collection<JetFile>): List<JetFile> =
            getFilesWithCallables(files).filter { getPackagePartFqName(it) == partFqName }

}