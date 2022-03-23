/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.tooling.core

inline fun <reified T : Any> extrasKey(
    name: String? = null,
    capabilities: List<Extras.Key.Capability<T>> = emptyList()
): Extras.Key<T> {
    return Extras.Key(Extras.Id(reifiedTypeSignatureOf(), name), capabilities)
}

fun emptyExtras(): Extras = EmptyExtras

fun mutableExtrasOf(): MutableExtras = MutableExtrasImpl()

fun mutableExtrasOf(vararg entries: Extras.Entry<*>): MutableExtras =
    MutableExtrasImpl(entries.toList())

infix fun <T : Any> Extras.Key<T>.value(value: T): Extras.Entry<T> = Extras.Entry(this, value)

interface Extras {
    class Id<T : Any> constructor(
        val type: ReifiedTypeSignature<T>,
        val name: String? = null,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Id<*>) return false
            if (name != other.name) return false
            if (type != other.type) return false
            return true
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + type.hashCode()
            return result
        }

        override fun toString(): String {
            if (name == null) return type.toString()
            return "$name: $type"
        }
    }

    class Key<T : Any> constructor(
        val id: Id<T>,
        val capabilities: List<Capability<T>> = emptyList()
    ) {

        interface Capability<T>

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is Key<*>) return false
            if (other.id != this.id) return false
            if (other.capabilities != this.capabilities) return false
            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + capabilities.hashCode()
            return result
        }

        override fun toString(): String = "Key($id)"

        fun withCapability(capability: Capability<T>): Key<T> {
            return Key(id = id, capabilities + capability)
        }

        inline fun <reified C : Capability<T>> findCapability(): C? {
            capabilities.forEach { capability -> if (capability is C) return capability }
            return null
        }


    }

    class Entry<T : Any>(val key: Key<T>, val value: T) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Entry<*>) return false
            if (other.key != key) return false
            if (other.value != value) return false
            return true
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        operator fun component1() = key
        operator fun component2() = value
    }

    val ids: Set<Id<*>>
    operator fun <T : Any> get(key: Key<T>): T?
    operator fun <T : Any> contains(id: Id<T>): Boolean
}


interface IterableExtras : Extras, Iterable<Extras.Entry<*>> {
    val keys: Set<Extras.Key<*>>
    val entries: Set<Extras.Entry<*>>
    override fun iterator(): Iterator<Extras.Entry<*>> = entries.iterator()
}

internal object EmptyExtras : IterableExtras {
    override val ids: Set<Extras.Id<*>> = emptySet()
    override val keys: Set<Extras.Key<*>> = emptySet()
    override val entries: Set<Extras.Entry<*>> = emptySet()
    override fun <T : Any> get(key: Extras.Key<T>): T? = null
    override fun <T : Any> contains(id: Extras.Id<T>): Boolean = false
}

interface MutableExtras : IterableExtras {
    /**
     * @param value: The new value, or null if the value shall be removed
     * @return The previous value or null if no previous value was set
     */
    operator fun <T : Any> set(key: Extras.Key<T>, value: T?): T?
}

/**
 * @return the previously set value, when present
 */
fun <T : Any> MutableExtras.remove(key: Extras.Key<T>): T? {
    return set(key, null)
}

@Suppress("unchecked_cast")
internal class MutableExtrasImpl private constructor(
    private val extras: MutableMap<Extras.Key<*>, Any>
) : MutableExtras {

    constructor() : this(mutableMapOf())

    constructor(entries: Iterable<Extras.Entry<*>>) :
            this(entries.associateTo(mutableMapOf()) { it.key to it.value })

    override val ids: Set<Extras.Id<*>>
        @Synchronized get() = extras.keys.map { it.id }.toSet()

    override val keys: Set<Extras.Key<*>>
        @Synchronized get() = extras.keys.toSet()

    override val entries: Set<Extras.Entry<*>>
        @Synchronized get() = extras.entries
            .map { (key, value) -> Extras.Entry(key as Extras.Key<Any>, value) }.toSet()

    @Synchronized
    override fun <T : Any> set(key: Extras.Key<T>, value: T?): T? {
        return if (value == null) extras.remove(key)?.let { it as T }
        else extras.put(key, value)?.let { it as T }
    }

    @Synchronized
    override fun <T : Any> get(key: Extras.Key<T>): T? {
        return extras[key]?.let { it as T }
    }

    @Synchronized
    override fun <T : Any> contains(id: Extras.Id<T>): Boolean {
        return ids.contains(id)
    }
}
