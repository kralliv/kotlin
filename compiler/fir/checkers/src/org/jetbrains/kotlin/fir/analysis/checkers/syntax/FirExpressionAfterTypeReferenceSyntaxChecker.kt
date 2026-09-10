/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.syntax

import com.intellij.lang.LighterASTNode
import com.intellij.util.diff.FlyweightCapableTreeStructure
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.checkers.requireFeatureSupport
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.isEnabled
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.util.getChildren
import org.jetbrains.kotlin.util.getNextSibling

sealed class FirExpressionAfterTypeReferenceSyntaxChecker<D : FirDeclaration> :
    FirDeclarationChecker<D>(MppCheckerKind.Common) {

    object Function : FirExpressionAfterTypeReferenceSyntaxChecker<FirFunction>() {

        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirFunction) {
            check(declaration.returnTypeRef)
        }
    }

    object ValueParameter : FirExpressionAfterTypeReferenceSyntaxChecker<FirValueParameter>() {

        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirValueParameter) {
            check(declaration.returnTypeRef)
        }
    }

    object Property : FirExpressionAfterTypeReferenceSyntaxChecker<FirProperty>() {

        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirProperty) {
            check(declaration.returnTypeRef)
        }
    }

    context(context: CheckerContext, reporter: DiagnosticReporter)
    protected fun check(type: FirTypeRef) {
        if (LanguageFeature.AllowExpressionAfterTypeReferenceWithoutSpacing.isEnabled()) return

        val source = type.source ?: return
        if (source.kind is KtFakeSourceElementKind) return

        if (!source.treeStructure.endsWithTypeArgumentList(source.lighterASTNode)) return

        val sibling = source.lighterASTNode.getNextSibling(source.treeStructure)
        if (sibling == null || !source.treeStructure.startsWithExpressionBlock(sibling)) return

        type.requireFeatureSupport(LanguageFeature.AllowExpressionAfterTypeReferenceWithoutSpacing)
    }

    private fun FlyweightCapableTreeStructure<LighterASTNode>.endsWithTypeArgumentList(node: LighterASTNode): Boolean {
        val children = node.getChildren(this)
        if (children.isEmpty()) return node.tokenType == KtTokens.GT
        return endsWithTypeArgumentList(children.last())
    }

    private fun FlyweightCapableTreeStructure<LighterASTNode>.startsWithExpressionBlock(node: LighterASTNode): Boolean {
        val children = node.getChildren(this)
        if (children.isEmpty()) return node.tokenType == KtTokens.EQ
        return startsWithExpressionBlock(children.first())
    }
}
