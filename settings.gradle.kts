pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "flower"

include(":flower-core")
include(":app")
include(":compose-app")
include(":flower-retrofit")
include("flower-ktorfit")
include(":xml-app")
