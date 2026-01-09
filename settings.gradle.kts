pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.2"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) {
            for (loader in loaders) {
                version("$name-$loader", mcVersion)
            }
        }

        mc("26.1.0", loaders = listOf("fabric"))
        mc("1.21.11", loaders = listOf("fabric", "neoforge"))
        mc("1.21.10", loaders = listOf("fabric", "neoforge"))
        mc("1.21.6", loaders = listOf("fabric", "neoforge"))
        mc("1.21.5", loaders = listOf("fabric", "neoforge"))
        mc("1.21.4", loaders = listOf("fabric", "neoforge"))
        mc("1.21.3", loaders = listOf("fabric", "neoforge"))
        mc("1.21.1", loaders = listOf("fabric", "neoforge"))
    }
}
rootProject.name = "YetAnotherConfigLib"
