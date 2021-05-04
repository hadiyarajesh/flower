plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.hadiyarajesh.flowersample"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                ))

            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    val lifecycleVersion = "2.3.0"
    val lifecycleExtVersion = "2.2.0"
    val roomVersion = "2.2.6"
    val retrofitVersion = "2.9.0"
    val moshiVersion = "1.11.0"
    val koinVersion = "2.1.5"
    val materialVersion = "1.2.0"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.31")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.google.android.material:material:${materialVersion}")

    implementation(project(":Flower"))

    implementation("androidx.annotation:annotation:1.2.0")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:${lifecycleExtVersion}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${lifecycleVersion}")

    implementation("androidx.room:room-ktx:${roomVersion}")
    kapt("androidx.room:room-compiler:${roomVersion}")

    implementation("com.squareup.retrofit2:converter-moshi:${retrofitVersion}")

    implementation("com.squareup.moshi:moshi:${moshiVersion}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${moshiVersion}")

    implementation("org.koin:koin-android:${koinVersion}")
    implementation("org.koin:koin-androidx-viewmodel:${koinVersion}")
    implementation("org.koin:koin-androidx-scope:${koinVersion}")
    implementation("org.koin:koin-androidx-fragment:${koinVersion}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

}