import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

group = "io.github.hadiyarajesh.flower-core"
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
    macosX64()
    watchosArm64()
    watchosX64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()
    js(IR) {
        browser()
        nodejs()
        binaries.executable()
    }
    linuxX64 {
        binaries {
            executable()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val linuxX64Main by getting
        val jvmMain by getting
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
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()

    pom {
        name.set(project.name)
        description.set("Flower Core Library")
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