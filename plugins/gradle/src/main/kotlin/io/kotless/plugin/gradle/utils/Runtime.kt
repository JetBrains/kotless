package io.kotless.plugin.gradle.utils

import io.kotless.CloudPlatform
import io.kotless.KotlessConfig
import io.kotless.resource.Lambda.Config.Runtime
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

internal fun Project.getTargetVersion(): JavaVersion? {
    val target = tasks.withType(KotlinJvmCompile::class.java).map { it.kotlinOptions.jvmTarget }.distinct()
    if (target.size > 1) {
        return null
    }

    return JavaVersion.toVersion(target.singleOrNull() ?: "1.6")
}

@Suppress("UnstableApiUsage")
internal fun Runtime.isCompatible(target: JavaVersion) = when (this) {
    Runtime.Java8 -> JavaVersion.VERSION_1_8.isCompatibleWith(target)
    Runtime.Java11 -> JavaVersion.VERSION_11.isCompatibleWith(target)
    Runtime.Java17 -> JavaVersion.VERSION_17.isCompatibleWith(target)
    Runtime.GraalVM -> JavaVersion.VERSION_11.isCompatibleWith(target)
    Runtime.Provided -> true
}

internal fun Project.getRuntimeVersion(target: JavaVersion, config: KotlessConfig): Runtime? {
    if (config.cloud.platform == CloudPlatform.Azure) return Runtime.Java8
    if (Runtime.Java8.isCompatible(target)) return Runtime.Java8
    if (Runtime.Java11.isCompatible(target)) return Runtime.Java11
    if (Runtime.Java17.isCompatible(target)) return Runtime.Java17

    return null
}

