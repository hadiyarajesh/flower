plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()
    js(IR) {
        browser()
        nodejs()
        binaries.executable()
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":flower-core"))
                api("de.jensklingenberg.ktorfit:ktorfit-lib:1.0.0-beta09")
            }
        }
        val jvmMain by getting
        val nativeMain by getting
        val jsMain by getting

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspAndroid","de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspIosX64","de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspJvm","de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspJs","de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspIosSimulatorArm64","de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
}