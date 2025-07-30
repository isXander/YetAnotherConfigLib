plugins {
    base
    kotlin("jvm") version "2.0.21" apply false

    id("dev.kikugie.stonecutter")

    val modstitchVersion = "0.6.3-unstable"
    id("dev.isxander.modstitch.base") version modstitchVersion apply false

    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
    id("org.ajoberstar.grgit") version "5.0.+" apply false
}
stonecutter active file("versions/current")

allprojects {
    repositories {
        maven("https://maven.terraformersmc.com")
        maven("https://maven.isxander.dev/releases")
        maven("https://maven.isxander.dev/snapshots")
        maven("https://maven.quiltmc.org/repository/release")
        exclusiveContent {
            forRepository { maven("https://thedarkcolour.github.io/KotlinForForge/") }
            filter { includeGroup("thedarkcolour") }
        }
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

stonecutter {
    parameters {
        fun String.propDefined() = project(node!!.metadata.project).findProperty(this)?.toString()?.isNotBlank() ?: false
        consts(listOf(
            "controlify" to "deps.controlify".propDefined(),
            "mod-menu" to "deps.modMenu".propDefined(),
        ))
    }
}

version = property("modVersion") as String

tasks.clean {
    delete(layout.buildDirectory.dir("finalJars"))
}

