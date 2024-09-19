/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.test.klib;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.junit.jupiter.api.Tag;
import org.jetbrains.kotlin.konan.test.blackbox.support.group.FirPipeline;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.GenerateNativeTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("native/native.tests/testData/klib/cross-compilation/identity")
@TestDataPath("$PROJECT_ROOT")
@Tag("frontend-fir")
@FirPipeline()
public class FirKlibCrossCompilationIdentityTestGenerated extends AbstractFirKlibCrossCompilationIdentityTest {
  @Test
  public void testAllFilesPresentInIdentity() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("native/native.tests/testData/klib/cross-compilation/identity"), Pattern.compile("^(.+)\\.kt$"), null, TargetBackend.NATIVE, true);
  }

  @Test
  @TestMetadata("simpleReferenceToDarwinApi.kt")
  public void testSimpleReferenceToDarwinApi() {
    runTest("native/native.tests/testData/klib/cross-compilation/identity/simpleReferenceToDarwinApi.kt");
  }

  @Test
  @TestMetadata("simpleSmoke.kt")
  public void testSimpleSmoke() {
    runTest("native/native.tests/testData/klib/cross-compilation/identity/simpleSmoke.kt");
  }

  @Test
  @TestMetadata("stdlibReferences.kt")
  public void testStdlibReferences() {
    runTest("native/native.tests/testData/klib/cross-compilation/identity/stdlibReferences.kt");
  }
}
