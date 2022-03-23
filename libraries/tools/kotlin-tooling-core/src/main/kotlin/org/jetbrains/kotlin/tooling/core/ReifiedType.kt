/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.tooling.core

import kotlin.reflect.KType
import kotlin.reflect.typeOf

@OptIn(UnsafeApi::class, ExperimentalStdlibApi::class)
inline fun <reified T> reifiedTypeOf(): ReifiedType<T> = ReifiedType(typeOf<T>())

class ReifiedType<T>
@UnsafeApi("Use 'reifiedTypeOf' instead")
@PublishedApi internal constructor(val raw: KType) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReifiedType<*>) return false
        if (other.raw != this.raw) return false
        return true
    }

    override fun hashCode(): Int {
        return 31 * raw.hashCode()
    }

    override fun toString(): String {
        return raw.toString()
    }
}

@OptIn(UnsafeApi::class)
val <T> ReifiedType<T>.signature: ReifiedTypeSignature<T>
    get() = ReifiedTypeSignature(reifiedTypeSignatureOf(raw))
