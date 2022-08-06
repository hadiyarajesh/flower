// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("multiplatform") version "1.7.10" apply false
}

buildscript {
    val hiltVersion by extra("2.43")
    val composeUiVersion by extra("1.2.0")

    allprojects {
        repositories {
            google()
            mavenLocal()
            mavenCentral()
            gradlePluginPortal()
        }
    }

    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.20.0")
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
    }
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}
