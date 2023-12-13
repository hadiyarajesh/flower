import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.gradleMavenPublish)
    id("maven-publish")
    id("signing")
}

group = "io.github.hadiyarajesh.flower-retrofit"
version = "3.3.0"

android {
    namespace = "com.hadiyarajesh.flower_retrofit"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    api(project(":flower-core"))
    implementation(libs.retrofit)
}

tasks.register<Jar>("androidSourcesJar") {
    group = "build"
    description = "Assemble sources"

    archiveClassifier.set("sources")
    if (project.plugins.hasPlugin("com.android.library")) {
        from(android.sourceSets["main"].java.srcDirs)
    } else {
        from(sourceSets["main"].allSource)
    }
}

mavenPublishing {
    publishToMavenCentral(host = SonatypeHost.S01, automaticRelease = true)
    signAllPublications()

    pom {
        name.set(project.name)
        description.set("Flower Retrofit Library")
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
