plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.hadiyarajesh.flower"
    compileSdk = 32
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdk = 21
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles(
            file("consumer-rules.pro")
        )
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
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

tasks.register("sourceJar", Jar::class) {
}

val PUBLISH_GROUP_ID by extra("io.github.hadiyarajesh")
val PUBLISH_VERSION by extra("2.0.3")
val PUBLISH_ARTIFACT_ID by extra("flower")
val PUBLISH_DESCRIPTION by extra("Flower is an Android library that makes networking and database caching easy. It enables developers to fetch network resources and use them as is OR combine them with local database at single place with fault tolerant architecture.")
val PUBLISH_URL by extra("https://github.com/hadiyarajesh/flower")
val PUBLISH_LICENSE_NAME by extra("MIT License")
val PUBLISH_LICENSE_URL by extra("https://github.com/hadiyarajesh/flower/blob/master/LICENSE")
val PUBLISH_DEVELOPER_ID by extra("hadiyarajesh")
val PUBLISH_DEVELOPER_NAME by extra("Rajesh Hadiya")
val PUBLISH_DEVELOPER_EMAIL by extra("hadiarajesh007@gmail.com")
val PUBLISH_SCM_CONNECTION by extra("scm:git:github.com/hadiyarajesh/flower.git")
val PUBLISH_SCM_DEVELOPER_CONNECTION by extra("scm:git:ssh://github.com/hadiyarajesh/flower.git")
val PUBLISH_SCM_URL by extra("https://github.com/hadiyarajesh/flower")

apply(from = "${rootDir}/scripts/publish-module.gradle")
