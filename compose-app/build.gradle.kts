plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.hadiyarajesh.compose_app"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.hadiyarajesh.compose_app"
        minSdk = 21
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
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
    const val composeVersion = "1.2.0"
    const val composeCompilerVersion = "1.3.0"
    const val roomVersion = "2.4.2"
    const val retrofitVersion = "2.9.0"
    const val moshiVersion = "1.13.0"
    const val accompanistVersion = "0.25.0"
}

dependencies {
    implementation("androidx.core:core-ktx:${rootProject.extra["coreKtxVersion"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.ui:ui:${LibVersion.composeVersion}")
    implementation("androidx.compose.ui:ui-tooling-preview:${LibVersion.composeVersion}")
    implementation("androidx.compose.material:material:${LibVersion.composeVersion}")
    implementation("androidx.navigation:navigation-compose:2.5.2")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")

    implementation("androidx.room:room-ktx:${LibVersion.roomVersion}")
    kapt("androidx.room:room-compiler:${LibVersion.roomVersion}")

    implementation(project(":flower-retrofit"))

    implementation("com.squareup.retrofit2:converter-moshi:${LibVersion.retrofitVersion}")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("com.squareup.moshi:moshi:${LibVersion.moshiVersion}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${LibVersion.moshiVersion}")

    implementation("com.google.accompanist:accompanist-swiperefresh:${LibVersion.accompanistVersion}")

    implementation("io.coil-kt:coil-compose:2.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${LibVersion.composeVersion}")
    debugImplementation("androidx.compose.ui:ui-tooling:${LibVersion.composeVersion}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${LibVersion.composeVersion}")
}

repositories {
    mavenCentral()
}
