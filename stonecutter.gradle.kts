import dev.kikugie.stonecutter.controller.ChiseledTask
import dev.kikugie.stonecutter.ide.RunConfigType

plugins {
    base
    kotlin("jvm") version "2.0.21" apply false

    id("dev.kikugie.stonecutter")

    val modstitchVersion = "0.5.14-unstable"
    id("dev.isxander.modstitch.base") version modstitchVersion apply false
    id("dev.isxander.modstitch.shadow") version modstitchVersion apply false

    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
    id("org.ajoberstar.grgit") version "5.0.+" apply false
}
stonecutter active file("versions/current")

val chiseledBuildAndCollect = registerChiseled("buildAndCollect")
val chiseledBuild = registerChiseled("build")
val chiseledReleaseModVersion = registerChiseled("releaseModVersion")
val chiseledPublishSnapshots = registerChiseled("publishAllPublicationsToXanderSnapshotsRepository", name = "chiseledPublishSnapshots")
val chiseledPublishToMaven = registerChiseled("publish", name = "chiseledPublishToMaven")
val chiseledRunTestmodClient = registerChiseled("runTestmodClient")

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
    generateRunConfigs = listOf(RunConfigType.SWITCH)

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

fun registerChiseled(task: String, name: String? = null, action: ChiseledTask.() -> Unit = {}): TaskProvider<ChiseledTask> {
    return tasks.register(
        name ?: ("chiseled" + task.replaceFirstChar { it.uppercase() }),
        stonecutter.chiseled.kotlin
    ) {
        group = "yacl"
        ofTask(task)
        action(this)

    }.also { stonecutter registerChiseled it }
}
