// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.android.application") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.6.10" apply false
}

buildscript {
    val hiltVersion by extra("2.42")
    val compose_ui_version by extra("1.1.1")

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

apply(from = "${rootDir}/scripts/publish-root.gradle")
