/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.idea.KotlinLanguage

class KtMarkerType(debugName: String) : IElementType(debugName, KotlinLanguage.INSTANCE) {

    fun createPsi(node: ASTNode): PsiElement = ASTWrapperPsiElement(node)
}
