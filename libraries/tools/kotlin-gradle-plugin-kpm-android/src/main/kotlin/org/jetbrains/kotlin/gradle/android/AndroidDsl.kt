/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(InternalKotlinGradlePluginApi::class)

package org.jetbrains.kotlin.gradle.android

import org.jetbrains.kotlin.gradle.kpm.idea.IdeaKotlinExtraSerializer
import org.jetbrains.kotlin.gradle.kpm.idea.InternalKotlinGradlePluginApi
import org.jetbrains.kotlin.tooling.core.HasExtras
import org.jetbrains.kotlin.tooling.core.HasMutableExtras
import org.jetbrains.kotlin.tooling.core.extrasKey
import java.io.File
import java.io.Serializable

internal val androidDslKey = extrasKey<AndroidDsl>()
    .withCapability(IdeaKotlinExtraSerializer.serializable())

val HasExtras.androidDsl: AndroidDsl? get() = extras[androidDslKey]

internal var HasMutableExtras.androidDsl: AndroidDsl?
    get() = extras[androidDslKey]
    set(value) {
        extras[androidDslKey] = value
    }

class AndroidDsl : Serializable {
    var compileSdk = 0
    var isMinifyEnabled: Boolean = false
    var androidManifest: File? = null
}
