// CHECK_TYPESCRIPT_DECLARATIONS
// SKIP_MINIFICATION
// SKIP_NODE_JS
// MODULE_KIND: COMMON_JS
// SKIP_ES_MODULES

@file:JsExport

package foo

val prop = 10

class C(val x: Int) {
    fun doubleX() = x * 2
}

fun box(): String = "OK"