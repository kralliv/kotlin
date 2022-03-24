/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.kpm.idea

import org.jetbrains.kotlin.gradle.kpm.idea.testFixtures.deserialize
import org.jetbrains.kotlin.gradle.kpm.idea.testFixtures.serialize
import org.jetbrains.kotlin.tooling.core.Extras
import org.jetbrains.kotlin.tooling.core.emptyExtras
import org.jetbrains.kotlin.tooling.core.extrasKey
import org.jetbrains.kotlin.tooling.core.mutableExtrasOf
import java.io.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull

class IdeaKotlinExtrasTest {

    private data class RetainedModel(val value: Int) : Serializable
    private data class UnretainedModel(val value: Int)

    private val retainedModelKey = extrasKey<RetainedModel>().withCapability(IdeaKotlinExtraSerializer.serializable())
    private val retainedModelKeyFoo = extrasKey<RetainedModel>("foo").withCapability(IdeaKotlinExtraSerializer.serializable())
    private val retainedModelKeyBar = extrasKey<RetainedModel>("bar").withCapability(IdeaKotlinExtraSerializer.serializable())

    private val unretainedModelKey = extrasKey<UnretainedModel>()
    private val unretainedModelKeyFoo = extrasKey<UnretainedModel>("foo")
    private val unretainedModelKeyBar = extrasKey<UnretainedModel>("bar")

    @Test
    fun `test - attach simple values`() {
        val extras = mutableExtrasOf()
        extras[retainedModelKey] = RetainedModel(1)
        extras[retainedModelKeyFoo] = RetainedModel(2)
        extras[retainedModelKeyBar] = RetainedModel(3)
        extras[unretainedModelKey] = UnretainedModel(4)
        extras[unretainedModelKeyFoo] = UnretainedModel(5)
        extras[unretainedModelKeyBar] = UnretainedModel(6)

        assertEquals(RetainedModel(1), extras[retainedModelKey])
        assertEquals(RetainedModel(2), extras[retainedModelKeyFoo])
        assertEquals(RetainedModel(3), extras[retainedModelKeyBar])
        assertEquals(UnretainedModel(4), extras[unretainedModelKey])
        assertEquals(UnretainedModel(5), extras[unretainedModelKeyFoo])
        assertEquals(UnretainedModel(6), extras[unretainedModelKeyBar])
    }

    @Test
    fun `test - accessing missing value`() {
        val extras = mutableExtrasOf()
        assertNull(extras[retainedModelKey])
        assertNull(extras[unretainedModelKey])
    }

    @Test
    fun `test - serializing extras`() {
        val extras = mutableExtrasOf()
        extras[retainedModelKey] = RetainedModel(1)
        extras[retainedModelKeyFoo] = RetainedModel(2)
        extras[retainedModelKeyBar] = RetainedModel(3)
        extras[unretainedModelKey] = UnretainedModel(4)
        extras[unretainedModelKeyFoo] = UnretainedModel(5)
        extras[unretainedModelKeyBar] = UnretainedModel(6)

        val deserializedContainer = IdeaKotlinExtras(extras).serialize().deserialize<IdeaKotlinExtras>()

        assertEquals(RetainedModel(1), deserializedContainer[retainedModelKey])
        assertEquals(RetainedModel(2), deserializedContainer[retainedModelKeyFoo])
        assertEquals(RetainedModel(3), deserializedContainer[retainedModelKeyBar])
        assertNull(deserializedContainer[unretainedModelKey])
        assertNull(deserializedContainer[unretainedModelKeyFoo])
        assertNull(deserializedContainer[unretainedModelKeyBar])
    }

    @Test
    fun `test - serializing extras twice`() {
        val extras = mutableExtrasOf()
        extras[retainedModelKey] = RetainedModel(1)
        extras[unretainedModelKey] = UnretainedModel(4)

        val deserializedContainer = IdeaKotlinExtras(extras).serialize().deserialize<IdeaKotlinExtras>()
        assertEquals(RetainedModel(1), deserializedContainer[retainedModelKey])
        assertNull(deserializedContainer[unretainedModelKey])

        val twiceDeserializedContainer = deserializedContainer.serialize().deserialize<IdeaKotlinExtras>()
        assertEquals(RetainedModel(1), twiceDeserializedContainer[retainedModelKey])
        assertNull(twiceDeserializedContainer[unretainedModelKey])
    }

    @Test
    fun `test - accessing deserialized extras without serializer`() {
        val extras = mutableExtrasOf()
        extras[retainedModelKey] = RetainedModel(1)
        extras[retainedModelKeyFoo] = RetainedModel(2)

        val deserializedContainer = IdeaKotlinExtras(extras).serialize().deserialize<IdeaKotlinExtras>()
        assertEquals(RetainedModel(1), deserializedContainer[retainedModelKey])

        /* Accessing something already deserialized without deserializer: Returning null, because the keys do not match */
        assertNull(deserializedContainer[Extras.Key(retainedModelKey.id)])

        /* Accessing something not deserialized without deserializer: Cannot deserialize: Null to be lenient */
        assertNull(deserializedContainer[Extras.Key(retainedModelKeyFoo.id)])

        /* Accessing something previously requested but now with deserializer */
        assertEquals(RetainedModel(2), deserializedContainer[retainedModelKeyFoo])

        /* Now accessing retainedModelFoo without serializer should behave like above */
        assertNull(deserializedContainer[Extras.Key(retainedModelKeyFoo.id)])
    }

    @Test
    fun `test - empty extras are equal`() {
        val extras1 = mutableExtrasOf()
        val extras2 = mutableExtrasOf()

        assertNotSame(extras1, extras2)
        assertEquals(extras1, extras2)
        assertEquals(extras2, extras1)
        assertEquals(emptyExtras(), extras1)
        assertEquals(extras1, emptyExtras())
        assertEquals(emptyExtras(), extras2)
        assertEquals(extras2, emptyExtras())

        assertEquals(extras1.hashCode(), extras2.hashCode())
        assertEquals(extras1.hashCode(), emptyExtras().hashCode())
    }
}
