plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.hadiyarajesh.compose_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hadiyarajesh.compose_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = LibVersion.composeCompilerVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

object LibVersion {
    const val composeCompilerVersion = "1.5.3"
    const val roomVersion = "2.6.0"
    const val retrofitVersion = "2.9.0"
    const val moshiVersion = "1.14.0"
    const val accompanistVersion = "0.27.0"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.bundles.lifecycle)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose.ui.impl)
    implementation(libs.material3)
    implementation(libs.navigation.compose)
//    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")

//    implementation("androidx.core:core-ktx:${rootProject.extra["coreKtxVersion"]}")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
//    implementation("androidx.activity:activity-compose:1.6.1")
//    implementation(composeBom)
//    implementation("androidx.compose.material3:material3")
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.navigation:navigation-compose:2.5.3")

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

//    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
//    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
//    ksp("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
//    implementation("androidx.room:room-ktx:${LibVersion.roomVersion}")
//    ksp("androidx.room:room-compiler:${LibVersion.roomVersion}")

    implementation(project(":flower-retrofit"))

    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp.interceptor.logging)
//    implementation("com.squareup.retrofit2:converter-moshi:${LibVersion.retrofitVersion}")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
//    implementation("com.squareup.moshi:moshi:${LibVersion.moshiVersion}")
//    ksp("com.squareup.moshi:moshi-kotlin-codegen:${LibVersion.moshiVersion}")

//    implementation("com.google.accompanist:accompanist-swiperefresh:${LibVersion.accompanistVersion}")

    implementation(libs.coil)
    implementation(libs.accompanist.swiperefresh)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.bundles.compose.ui.debug)
}
