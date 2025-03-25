pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.isxander.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6-alpha.13"
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

        mc("1.21.5", loaders = listOf("fabric", "neoforge"))
        mc("1.21.4", loaders = listOf("fabric", "neoforge"))
        mc("1.21.3", loaders = listOf("fabric", "neoforge"))
        mc("1.21.1", loaders = listOf("fabric", "neoforge"))
        mc("1.20.6", loaders = listOf("fabric", "neoforge"))
        mc("1.20.4", loaders = listOf("fabric", "neoforge"))
        mc("1.20.1", loaders = listOf("fabric", "forge"))
    }
}
rootProject.name = "YetAnotherConfigLib"
