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

package org.jetbrains.kotlin.idea.stubindex;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClassOrObject;

import java.util.Collection;

public class JetSuperClassIndex extends StringStubIndexExtension<KtClassOrObject> {
    private static final StubIndexKey<String, KtClassOrObject> KEY = KotlinIndexUtil.createIndexKey(JetSuperClassIndex.class);

    private static final JetSuperClassIndex ourInstance = new JetSuperClassIndex();

    @NotNull
    public static JetSuperClassIndex getInstance() {
        return ourInstance;
    }

    private JetSuperClassIndex() {}

    @NotNull
    @Override
    public StubIndexKey<String, KtClassOrObject> getKey() {
        return KEY;
    }

    @NotNull
    @Override
    public Collection<KtClassOrObject> get(@NotNull String s, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        return super.get(s, project, JetSourceFilterScope.kotlinSourcesAndLibraries(scope, project));
    }
}
