plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":flower-core"))

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
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

publishing {
    publications {
        create<MavenPublication>("default") {
            artifact(tasks["androidSourcesJar"])

            pom {
                name.set(project.name)
                description.set("Flower Retrofit Library")
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
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url = when {
                    "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                    else -> property("sonatypeReleaseUrl")
                } as String
                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }
    }
}