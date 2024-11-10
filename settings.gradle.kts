pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
}

rootProject.name = "nba-widgets"
include(":app")
include(":module-nba-2k15")
include(":module-nba-2k16")
include(":module-common")
include(":module-looped-logos")
include(":module-nba-2k15-circle")
include(":module-nba-2k16-circle")
include(":module-looped-logos-circle")
include(":module-espn")
