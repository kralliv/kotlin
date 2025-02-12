// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// LANGUAGE: +AllowReifiedTypeInCatchClause
// KT-54363

inline fun <reified E : Exception, R> tryCatch(lazy: () -> R, failure: (E) -> R): R =
    try {
        lazy()
    } catch (e: E) {
        failure(e)
    }
