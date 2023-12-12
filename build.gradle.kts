// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("multiplatform") version "1.9.10" apply false
    id("com.vanniktech.maven.publish") version "0.22.0" apply false
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}

buildscript {
    val coreKtxVersion by extra("1.10.1")
    val hiltVersion by extra("2.48")
    val navSafeArgsVersion by extra("2.5.3")

    dependencies {
//        classpath("com.android.tools.build:gradle:8.1.1")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navSafeArgsVersion")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.22.0")
    }
}

//allprojects {
//    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//        kotlinOptions {
//            jvmTarget = JavaVersion.VERSION_1_8.toString()
//        }
//    }
//}