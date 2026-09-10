// RUN_PIPELINE_TILL: BACKEND

import kotlin.reflect.KFunction

fun test(x: Int, y: Int) {
    a(::a < x, y >= x)
}

fun a(a: Boolean, b: Boolean) {}

operator fun KFunction<*>.compareTo(value: Int): Int = 0

/* GENERATED_FIR_TAGS: comparisonExpression, functionDeclaration, ifExpression, integerLiteral */
