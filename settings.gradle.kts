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
rootProject.name = "HabitFlow"
// Single-module build — only :app
include(":app")
// NOTE: multi-module projects (:core, :data, :domain) not included
// All code lives in app/src/main/java/<package>/
