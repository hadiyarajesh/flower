import com.vanniktech.maven.publish.SonatypeHost

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
    namespace = "com.hadiyarajesh.flower_ktorfit"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        buildConfig = false
    }
}

kotlin {
    android {
        publishLibraryVariants("release")
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
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":flower-core"))
                api("de.jensklingenberg.ktorfit:ktorfit-lib:1.0.0-beta12")
            }
        }
        val jvmMain by getting
        val androidMain by getting
        val linuxX64Main by getting
        val jsMain by getting
        val mingwX64Main by getting
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
    publishToMavenCentral(host = SonatypeHost.S01, automaticRelease = true)
    signAllPublications()

    pom {
        name.set(project.name)
        description.set("Flower Ktorfit Library")
        url.set("https://github.com/hadiyarajesh/flower")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        scm {
            url.set("https://github.com/hadiyarajesh/flower")
            connection.set("scm:git:git://github.com/hadiyarajesh/flower.git")
        }

        developers {
            developer {
                id.set("hadiyarajesh")
                name.set("Rajesh Hadiya")
                url.set("https://github.com/hadiyarajesh")
            }

            developer {
                id.set("DatL4g")
                name.set("Jeff Retz")
                url.set("https://github.com/DatL4g")
            }
        }
    }
}
