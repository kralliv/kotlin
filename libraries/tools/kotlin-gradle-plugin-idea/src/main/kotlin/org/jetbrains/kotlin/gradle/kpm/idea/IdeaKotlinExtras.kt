/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.kpm.idea

import org.jetbrains.kotlin.tooling.core.*
import java.io.Serializable

sealed interface IdeaKotlinExtras : Extras, Serializable

fun IdeaKotlinExtras(): IdeaKotlinExtras = EmptyIdeaKotlinExtras

fun IdeaKotlinExtras(extras: IterableExtras): IdeaKotlinExtras {
    if (extras.isEmpty()) return EmptyIdeaKotlinExtras
    return IdeaKotlinExtrasImpl(extras.toExtras())
}

@WriteReplacedModel(replacedBy = IdeaKotlinExtrasSurrogate::class)
@InternalKotlinGradlePluginApi
private class IdeaKotlinExtrasImpl(private val extras: IterableExtras) : IdeaKotlinExtras {
    override val ids: Set<Extras.Id<*>> get() = extras.ids
    override fun <T : Any> get(key: Extras.Key<T>): T? = extras[key]
    override fun <T : Any> contains(id: Extras.Id<T>): Boolean = id in extras

    @Suppress("unchecked_cast")
    private fun writeReplace(): Any {
        return IdeaKotlinExtrasSurrogate(serialize(extras.entries))
    }
}

private object EmptyIdeaKotlinExtras : IdeaKotlinExtras {
    override val ids: Set<Extras.Id<*>> = emptySet()
    override fun <T : Any> get(key: Extras.Key<T>): T? = null
    override fun <T : Any> contains(id: Extras.Id<T>): Boolean = false

    @Suppress("unused")
    private const val serialVersionUID = 0L
}

private class SerializedExtrasEntry(val id: Extras.Id<*>, val data: ByteArray)

private class IdeaKotlinExtrasSurrogate(
    private val extras: Map<Extras.Id<*>, ByteArray>
) : Serializable {
    private fun readResolve(): Any {
        return SerializedIdeaKotlinExtras(extras.toMutableMap())
    }

    companion object {
        private const val serialVersionUID = 0L
    }
}

@WriteReplacedModel(replacedBy = IdeaKotlinExtrasSurrogate::class)
@Suppress("unchecked_cast")
private class SerializedIdeaKotlinExtras(
    private val serializedExtras: MutableMap<Extras.Id<*>, ByteArray>
) : IdeaKotlinExtras {
    private val deserializedNulls = mutableSetOf<Extras.Key<*>>()
    private val deserializedExtras = mutableExtrasOf()
    override val ids: Set<Extras.Id<*>> = serializedExtras.keys.toSet()

    override fun <T : Any> contains(id: Extras.Id<T>): Boolean = id in ids

    @Synchronized
    override fun <T : Any> get(key: Extras.Key<T>): T? {
        /* Check previous results */
        deserializedExtras[key]?.let { return it }
        if (key in deserializedNulls) return null

        /* try to deserialize */
        val data = serializedExtras[key.id] ?: return null
        val serializer = key.findCapability<IdeaKotlinExtraSerializer<T>>() ?: return null
        return serializer.deserialize(data).also { value ->
            /* Release memory in favor of deserialized value cache */
            serializedExtras.remove(key.id)

            /* Cache serialization result */
            if (value == null) deserializedNulls.add(key)
            else deserializedExtras[key] = value
        }
    }

    @Synchronized
    private fun writeReplace(): Any {
        return IdeaKotlinExtrasSurrogate(
            serializedExtras + serialize(deserializedExtras.entries)
        )
    }
}

private fun serialize(entries: Set<Extras.Entry<*>>): Map<Extras.Id<*>, ByteArray> {
    fun <T : Any> serializeOrNull(entry: Extras.Entry<T>): SerializedExtrasEntry? {
        val serializer = entry.key.findCapability<IdeaKotlinExtraSerializer<T>>() ?: return null
        return SerializedExtrasEntry(entry.key.id, serializer.serialize(entry.value))
    }

    return entries.mapNotNull { serializeOrNull(it) }.associate { it.id to it.data }
}
