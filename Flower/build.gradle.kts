plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdk = 21
        targetSdk = 31

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

tasks.register("sourceJar", Jar::class) {

}

dependencies {
    val coroutinesVersion = "1.5.2"
    val retrofitVersion = "2.9.0"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation("androidx.core:core-ktx:1.7.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutinesVersion}")
    api("com.squareup.retrofit2:retrofit:${retrofitVersion}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

val PUBLISH_GROUP_ID by extra("io.github.hadiyarajesh")
val PUBLISH_VERSION by extra("2.0.0")
val PUBLISH_ARTIFACT_ID by extra("flower")

apply(from = "${rootDir}/scripts/publish-module.gradle")