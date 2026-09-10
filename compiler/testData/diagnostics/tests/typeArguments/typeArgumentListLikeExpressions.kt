// ISSUE: KT-8263
// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE_FEATURE_TOGGLED: AllowTypeArgumentListLikeExpressions

fun f(x: Int, y: Int, z: () -> Any, f1: (Any?) -> Unit, f2: (Boolean, Boolean) -> Unit) {
    // type
    f1(z() as Int < 15)
    f1(z() as Int < y)
    f1(z() as Int < 15 > false)
    f2(z() as Int < 15, x > y)
    f1(z() is Int < true)
    f1(z() !is Int < true)
    f1(z() !is Int < true > false)
    f2(z() !is Int < true, x > y)

    // callSuffix
    f1(x<y, y>(x))
    f2(x<(y), y>x)
    f2(x<y, (y)>x)
    f2((x)<y, y>x)
    f2(x < y, (y > x))
    f2((x < y), y > x)
    f2(x < y, y > x)

    // callSuffix recovery
    f2(x < 1, 3 > y)
    f2(x < 1, (3) > y)
    f2(x < (1), 3 > y)
    f2(x < (1), (3) > y)
    f2(x < y, 3 > y)
    f2(x < y, (3) > y)
    f1(x < if (y > (0)) y else 0)
    f1(x < (if (y > (0)) y else 0))
    f1(x < when (y > (0)) { else -> 0 })
    f1(x < (when (y > (0)) { else -> 0 }))
}

typealias y = String
fun <A, B> x(x: Int): Int = x

/* GENERATED_FIR_TAGS: asExpression, comparisonExpression, functionDeclaration, functionalType, ifExpression,
integerLiteral, isExpression, nullableType, typeAliasDeclaration, typeParameter, whenExpression, whenWithSubject */
