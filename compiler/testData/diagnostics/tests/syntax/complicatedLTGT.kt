// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-8263

fun test(x: Int, y: Int) {
    if (x <!UNSUPPORTED_FEATURE!><<!> (if (y > 115) 1 else 2)) {
        Unit
    }
}

/* GENERATED_FIR_TAGS: functionDeclaration, functionalType, ifExpression, integerLiteral, lambdaLiteral */
