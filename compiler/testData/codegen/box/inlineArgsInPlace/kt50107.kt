inline fun foo1(a: Any?): Any? = a
inline fun Any?.foo2(): Any? = this

//inline fun bar1(c: (Any) -> Unit) = foo1(c) // Illegal usage of inline-parameter 'c' in 'public inline fun bar1(c: (Any) -> Unit): Any? defined in root package in file File.kt'. Add 'noinline' modifier to the parameter declaration
inline fun bar2(c: (Any) -> Unit) = c.foo2() // OK

fun qux(): Any? {
//  return bar1 { }
    return bar2 { }
}