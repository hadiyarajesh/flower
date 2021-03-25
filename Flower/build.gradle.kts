plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

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

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.hadiyarajesh"
                artifactId = "final"
                version = "1.0"

                from(components["release"])
            }
        }
    }
}
dependencies {

    val coroutinesVersion = "1.3.4"
    val retrofitVersion = "2.9.0"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.31")
    implementation("androidx.core:core-ktx:1.3.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutinesVersion}")
    implementation("com.squareup.retrofit2:retrofit:${retrofitVersion}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

}