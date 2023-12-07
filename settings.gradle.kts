pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.quiltmc.org/repository/release")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}

rootProject.name = "YetAnotherConfigLib"

val enabledLoaders = settings.extra.properties["loaders"].toString().split(",").map { it.trim() }

include("common")
include("test-common")

if ("fabric" in enabledLoaders) {
    include("fabric")
    include("test-fabric")
}

if ("neoforge" in enabledLoaders) {
    include("neoforge")
    include("test-neoforge")
}
