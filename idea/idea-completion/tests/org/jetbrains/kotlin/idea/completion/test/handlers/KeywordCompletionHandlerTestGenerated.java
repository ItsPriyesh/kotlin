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

package org.jetbrains.kotlin.idea.completion.test.handlers;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.JetTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/idea-completion/testData/handlers/keywords")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class KeywordCompletionHandlerTestGenerated extends AbstractKeywordCompletionHandlerTest {
    @TestMetadata("AddCompanionToObject.kt")
    public void testAddCompanionToObject() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/AddCompanionToObject.kt");
        doTest(fileName);
    }

    public void testAllFilesPresentInKeywords() throws Exception {
        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/idea-completion/testData/handlers/keywords"), Pattern.compile("^(.+)\\.kt$"), true);
    }

    @TestMetadata("Break.kt")
    public void testBreak() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/Break.kt");
        doTest(fileName);
    }

    @TestMetadata("CompanionObject.kt")
    public void testCompanionObject() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/CompanionObject.kt");
        doTest(fileName);
    }

    @TestMetadata("FileKeyword.kt")
    public void testFileKeyword() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/FileKeyword.kt");
        doTest(fileName);
    }

    @TestMetadata("Getter1.kt")
    public void testGetter1() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/Getter1.kt");
        doTest(fileName);
    }

    @TestMetadata("Getter2.kt")
    public void testGetter2() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/Getter2.kt");
        doTest(fileName);
    }

    @TestMetadata("NoSpaceAfterNull.kt")
    public void testNoSpaceAfterNull() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/NoSpaceAfterNull.kt");
        doTest(fileName);
    }

    @TestMetadata("QualifiedReturnNonUnit.kt")
    public void testQualifiedReturnNonUnit() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/QualifiedReturnNonUnit.kt");
        doTest(fileName);
    }

    @TestMetadata("QualifiedReturnNonUnitExplicit.kt")
    public void testQualifiedReturnNonUnitExplicit() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/QualifiedReturnNonUnitExplicit.kt");
        doTest(fileName);
    }

    @TestMetadata("QualifiedReturnUnit.kt")
    public void testQualifiedReturnUnit() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/QualifiedReturnUnit.kt");
        doTest(fileName);
    }

    @TestMetadata("ReturnEmptyList.kt")
    public void testReturnEmptyList() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/ReturnEmptyList.kt");
        doTest(fileName);
    }

    @TestMetadata("ReturnInEmptyType.kt")
    public void testReturnInEmptyType() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/ReturnInEmptyType.kt");
        doTest(fileName);
    }

    @TestMetadata("ReturnInProperty.kt")
    public void testReturnInProperty() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/ReturnInProperty.kt");
        doTest(fileName);
    }

    @TestMetadata("ReturnInTypeFunction.kt")
    public void testReturnInTypeFunction() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/ReturnInTypeFunction.kt");
        doTest(fileName);
    }

    @TestMetadata("ReturnInUnit.kt")
    public void testReturnInUnit() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/ReturnInUnit.kt");
        doTest(fileName);
    }

    @TestMetadata("ReturnNull.kt")
    public void testReturnNull() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/ReturnNull.kt");
        doTest(fileName);
    }

    @TestMetadata("Setter1.kt")
    public void testSetter1() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/Setter1.kt");
        doTest(fileName);
    }

    @TestMetadata("Setter2.kt")
    public void testSetter2() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/Setter2.kt");
        doTest(fileName);
    }

    @TestMetadata("SpaceAfterImport.kt")
    public void testSpaceAfterImport() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/SpaceAfterImport.kt");
        doTest(fileName);
    }

    @TestMetadata("UseSiteAnnotationTarget1.kt")
    public void testUseSiteAnnotationTarget1() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/UseSiteAnnotationTarget1.kt");
        doTest(fileName);
    }

    @TestMetadata("UseSiteAnnotationTarget2.kt")
    public void testUseSiteAnnotationTarget2() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/UseSiteAnnotationTarget2.kt");
        doTest(fileName);
    }

    @TestMetadata("UseSiteAnnotationTarget3.kt")
    public void testUseSiteAnnotationTarget3() throws Exception {
        String fileName = JetTestUtils.navigationMetadata("idea/idea-completion/testData/handlers/keywords/UseSiteAnnotationTarget3.kt");
        doTest(fileName);
    }
}
