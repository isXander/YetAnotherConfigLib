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
    id("dev.kikugie.stonecutter") version "0.7.6"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) {
            for (loader in loaders) {
                vers("$name-$loader", mcVersion)
            }
        }

        mc("1.21.9", loaders = listOf("fabric", "neoforge"))
        mc("1.21.6", loaders = listOf("fabric", "neoforge"))
        mc("1.21.5", loaders = listOf("fabric", "neoforge"))
        mc("1.21.4", loaders = listOf("fabric", "neoforge"))
        mc("1.21.3", loaders = listOf("fabric", "neoforge"))
        mc("1.21.1", loaders = listOf("fabric", "neoforge"))
    }
}
rootProject.name = "YetAnotherConfigLib"
