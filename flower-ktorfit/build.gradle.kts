plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
}

group = "io.github.hadiyarajesh.flower-ktorfit"
version = "3.0.0"

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

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01)
    signAllPublications()

    pom {
        name.set(project.name)
        description.set("Flower Ktorfit Library")
        url.set("https://github.com/hadiyarajesh/flower")

        licenses {
            license {
                name.set("MIT")
                url.set("https://github.com/hadiyarajesh/flower/blob/master/LICENSE")
            }
        }
        scm {
            url.set("https://github.com/hadiyarajesh/flower")
            connection.set("scm:git:git://github.com/hadiyarajesh/flower.git")
        }
        developers {
            developer {
                name.set("Rajesh Hadiya")
                url.set("https://github.com/hadiyarajesh")
            }
            developer {
                name.set("Jeff Retz")
                url.set("https://github.com/DatL4g")
            }
        }
    }
}