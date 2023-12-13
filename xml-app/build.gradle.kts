plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.hadiyarajesh.xml_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hadiyarajesh.xml_app"
        minSdk = 24
        targetSdk = 34
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
        buildConfig = true
    }

    packagingOptions {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(project(":flower-retrofit"))

    implementation(libs.appcompat)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.core.ktx)

    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.material)
    implementation(libs.swiperefreshlayout)

    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp.interceptor.logging)

    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.coil)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Pass options to Room ksp processor
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
    arg("room.generateKotlin", "true")
}
