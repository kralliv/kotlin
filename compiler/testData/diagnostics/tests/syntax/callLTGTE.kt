// RUN_PIPELINE_TILL: BACKEND

fun test(x: Int, y: Int) {
    a(x < y, y >= x)
}

fun a(a: Boolean, b: Boolean) {}

/* GENERATED_FIR_TAGS: comparisonExpression, functionDeclaration, ifExpression, integerLiteral */
