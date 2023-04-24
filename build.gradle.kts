plugins {
    alias(libs.plugins.architectury.plugin)
    alias(libs.plugins.architectury.loom) apply false
    alias(libs.plugins.unified.publishing) apply false
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "architectury-plugin")

    version = "1.0.0+1.19.4"
    group = "dev.isxander"

    pluginManager.withPlugin("base") {
        val base = the<BasePluginExtension>()

        base.archivesName.set("yet-another-config-lib")
    }

    repositories {
        mavenCentral()
        maven("https://maven.terraformersmc.com/releases")
        maven("https://maven.isxander.dev/releases")
        maven("https://maven.isxander.dev/snapshots")
        maven("https://maven.quiltmc.org/repository/release")
        maven("https://api.modrinth.com/maven") {
            name = "Modrinth"
            content {
                includeGroup("maven.modrinth")
            }
        }
        maven("https://jitpack.io")
    }
}

tasks.register("releaseMod") {
    group = "mod"
}
tasks.register("buildAll") {
    group = "mod"

    dependsOn(project(":fabric").tasks["build"])
    dependsOn(project(":forge").tasks["build"])
}
