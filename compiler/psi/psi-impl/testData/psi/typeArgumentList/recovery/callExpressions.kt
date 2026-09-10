// COMPILATION_ERRORS

fun f1() {
    call<x>()
    call<x> { }
    call<x>::
    call<x>[0]
    call<x>?
    call<x>?.
    call<x>.
    call<x>!!
}

fun f2() {
    a(call<x>())
    a(call<x> { })
    a(call<x>::)
    a(call<x>[0])
    a(call<x>?)
    a(call<x>?.)
    a(call<x>.)
    a(call<x>!!)
}

fun f3() {
    call<x>!!false
}

fun f4() {
    a(call<x>!!false)
}
