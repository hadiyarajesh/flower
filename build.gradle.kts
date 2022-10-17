// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("multiplatform") version "1.7.10" apply false
    id("com.vanniktech.maven.publish") version "0.22.0" apply false
}

buildscript {
    val coreKtxVersion by extra("1.9.0")
    val hiltVersion by extra("2.43")

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
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.22.0")
    }
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}
