plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.7.10"
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}

android {
    namespace = "com.hadiyarajesh.compose_app"
    compileSdk = 32

    defaultConfig {
        applicationId = "com.hadiyarajesh.compose_app"
        minSdk = 21
        targetSdk = 32
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
        kotlinCompilerExtensionVersion = "1.3.0-rc02"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

object LibVersion {
    const val composeVersion = "1.2.0-beta03"
    const val roomVersion = "2.4.2"
    const val retrofitVersion = "2.9.0"
    const val moshiVersion = "1.13.0"
    const val accompanistVersion = "0.25.0"
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.compose.ui:ui:${LibVersion.composeVersion}")
    implementation("androidx.compose.ui:ui-tooling-preview:${LibVersion.composeVersion}")
    implementation("androidx.compose.material:material:${LibVersion.composeVersion}")
    implementation("androidx.navigation:navigation-compose:2.5.1")
    implementation("androidx.paging:paging-compose:1.0.0-alpha16")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")

    implementation("androidx.room:room-ktx:${LibVersion.roomVersion}")
    implementation("androidx.room:room-paging:${LibVersion.roomVersion}")
    kapt("androidx.room:room-compiler:${LibVersion.roomVersion}")

    implementation("io.coil-kt:coil-compose:2.1.0")

    implementation("com.google.accompanist:accompanist-swiperefresh:${LibVersion.accompanistVersion}")

    implementation(project(":flower-ktorfit"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${LibVersion.composeVersion}")
    debugImplementation("androidx.compose.ui:ui-tooling:${LibVersion.composeVersion}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${LibVersion.composeVersion}")

    implementation("io.ktor:ktor-client-core:2.0.3")
    implementation("io.ktor:ktor-client-cio:2.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")

    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
}
repositories {
    mavenCentral()
}
