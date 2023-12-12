// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.gradleMavenPublish) apply false
    kotlin("multiplatform") version "1.9.10" apply false
}

buildscript {
    val coreKtxVersion by extra("1.10.1")
    val hiltVersion by extra("2.48")
    val navSafeArgsVersion by extra("2.5.3")

    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navSafeArgsVersion")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.22.0")
    }
}
