// LANGUAGE: +ContextSensitiveResolutionUsingExpectedType
// ISSUE: KT-75977

sealed interface Simple {
    class Left: Simple
    class Right: Simple
}

fun testWithSubject(s: Simple) = when(s) {
    !is Left -> "not a left"
    !is Right -> "not a right"
    else -> ""
}
