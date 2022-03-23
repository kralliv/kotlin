/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(InternalKotlinGradlePluginApi::class)

package org.jetbrains.kotlin.gradle.android

import org.jetbrains.kotlin.gradle.kpm.KotlinExternalModelKey
import org.jetbrains.kotlin.gradle.kpm.KotlinExternalModelSerializer
import org.jetbrains.kotlin.gradle.kpm.idea.IdeaKotlinFragment
import org.jetbrains.kotlin.gradle.kpm.idea.InternalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.KotlinGradleFragment
import org.jetbrains.kotlin.tooling.core.extrasKey
import java.io.File
import java.io.Serializable

val androidDslKey = KotlinExternalModelKey<AndroidDsl>(KotlinExternalModelSerializer.serializable())

val androidExtrasKey = extrasKey<AndroidDsl>()

class AndroidDsl : Serializable {
    var compileSdk = 0
    var isMinifyEnabled: Boolean = false
    var androidManifest: File? = null
}


fun playground(fragment: KotlinGradleFragment) {
    val previousDsl = fragment.extras.set(androidExtrasKey, AndroidDsl())
    val dsl = fragment.extras[androidExtrasKey]
}
