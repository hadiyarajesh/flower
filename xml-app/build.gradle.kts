/*
 *  Copyright (C) 2023 Rajesh Hadiya
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
}

android {
    namespace = "com.example.xml_app"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.xml_app"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
    }

    packagingOptions {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

object LibVersion {
    const val roomVersion = "2.4.2"
    const val retrofitVersion = "2.9.0"
    const val moshiVersion = "1.14.0"
    const val navigationVersion = "2.5.3"
    const val lifecycleVersion = "2.6.1"
    const val coilVersion = "2.3.0"
    const val loggingInterceptorVersion = "4.10.0"
    const val swipeRefreshLayoutVersion = "1.1.0"
    const val coreKtxVersion = "1.9.0"
    const val appCompatVersion = "1.6.1"
    const val materialVersion = "1.8.0"
    const val constraintLayoutVersion = "2.1.4"
    const val recyclerViewVersion = "1.3.0"
    const val flowerRetrofitVersion = "3.1.0"
}

dependencies {
    // Kotlin-navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${LibVersion.navigationVersion}")
    implementation("androidx.navigation:navigation-ui-ktx:${LibVersion.navigationVersion}")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:${LibVersion.swipeRefreshLayoutVersion}")

    implementation("androidx.core:core-ktx:${LibVersion.coreKtxVersion}")
    implementation("androidx.appcompat:appcompat:${LibVersion.appCompatVersion}")
    implementation("com.google.android.material:material:${LibVersion.materialVersion}")
    implementation("androidx.constraintlayout:constraintlayout:${LibVersion.constraintLayoutVersion}")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${LibVersion.lifecycleVersion}")
    // Lifecycles only (without ViewModel or LiveData)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${LibVersion.lifecycleVersion}")
    // Saved state module for ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${LibVersion.lifecycleVersion}")
    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    implementation("androidx.lifecycle:lifecycle-process:${LibVersion.lifecycleVersion}")

    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")

    implementation("androidx.room:room-ktx:${LibVersion.roomVersion}")
    kapt("androidx.room:room-compiler:${LibVersion.roomVersion}")

    //implementation(project(":flower-retrofit"))
    implementation("io.github.hadiyarajesh.flower-retrofit:flower-retrofit:${LibVersion.flowerRetrofitVersion}") {
        because("Flower simplifies networking and database caching on Android/Multiplatform")
    }

    //RecyclerView
    implementation("androidx.recyclerview:recyclerview:${LibVersion.recyclerViewVersion}")
    // For control over item selection of both touch and mouse driven selection
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:${LibVersion.retrofitVersion}")
    implementation("com.squareup.retrofit2:converter-moshi:${LibVersion.retrofitVersion}")
    implementation("com.squareup.okhttp3:logging-interceptor:${LibVersion.loggingInterceptorVersion}")

    implementation("com.squareup.moshi:moshi:${LibVersion.moshiVersion}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${LibVersion.moshiVersion}")

    // Coil for image loading
    implementation("io.coil-kt:coil:${LibVersion.coilVersion}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}