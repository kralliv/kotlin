/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.syntax

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.findChildByType
import org.jetbrains.kotlin.diagnostics.findDescendantByType
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFileChecker
import org.jetbrains.kotlin.fir.analysis.checkers.requireFeatureSupport
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.expressions.FirComparisonExpression
import org.jetbrains.kotlin.fir.expressions.FirOperation
import org.jetbrains.kotlin.fir.isEnabled
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.visitors.FirDefaultVisitorVoid

object FirTypeArgumentListLikeExpressionChecker : FirFileChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirFile) {
        if (LanguageFeature.AllowTypeArgumentListLikeExpressions.isEnabled()) return

        declaration.accept(object : FirDefaultVisitorVoid() {
            val tracker = Tracker()

            fun anyChildMarkedAsTypeArgumentListLikeExpression(element: FirElement): Boolean {
                tracker.enterNested()
                element.acceptChildren(this)
                return tracker.leaveNested()
            }

            fun markAsTypeArgumentListLikeExpression() {
                tracker.mark()
            }

            override fun visitElement(element: FirElement) {
                if (anyChildMarkedAsTypeArgumentListLikeExpression(element)) {
                    markAsTypeArgumentListLikeExpression()
                }
            }

            override fun visitComparisonExpression(comparisonExpression: FirComparisonExpression) {
                if (anyChildMarkedAsTypeArgumentListLikeExpression(comparisonExpression)
                    || isMarkedAsTypeArgumentListLikeExpression(comparisonExpression)
                ) {
                    comparisonExpression.requireFeatureSupport(
                        LanguageFeature.AllowTypeArgumentListLikeExpressions,
                        SourceElementPositioningStrategies.OPERATOR,
                    )
                }
            }

            fun isMarkedAsTypeArgumentListLikeExpression(comparisonExpression: FirComparisonExpression): Boolean {
                val source = comparisonExpression.source ?: return false
                if (source.kind is KtFakeSourceElementKind) return false

                return comparisonExpression.operation == FirOperation.LT && source.treeStructure.findChildByType(
                    source.lighterASTNode,
                    KtNodeTypes.TYPE_ARGUMENT_LIST_LIKE_EXPRESSION
                ) != null
            }

            override fun visitTypeRef(typeRef: FirTypeRef) {
                if (isMarkedAsTypeArgumentListLikeExpression(typeRef)) {
                    markAsTypeArgumentListLikeExpression()
                }
            }

            fun isMarkedAsTypeArgumentListLikeExpression(typeRef: FirTypeRef): Boolean {
                val source = typeRef.source ?: return false
                if (source.kind is KtFakeSourceElementKind) return false

                return source.treeStructure.findDescendantByType(
                    source.lighterASTNode,
                    KtNodeTypes.TYPE_ARGUMENT_LIST_LIKE_EXPRESSION
                ) != null
            }
        })
    }

    private class Tracker {
        private val stack = ArrayList<Boolean>(64)

        fun enterNested() {
            stack.add(false)
        }

        fun leaveNested(): Boolean {
            return stack.removeLast()
        }

        fun mark() {
            if (stack.isEmpty()) return
            stack[stack.lastIndex] = true
        }
    }
}
