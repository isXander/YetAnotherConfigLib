plugins {
    kotlin("jvm") version "2.3.0" apply false

    id("dev.kikugie.stonecutter")

    val modstitchVersion = "0.8.4"
    id("dev.isxander.modstitch.base") version modstitchVersion apply false
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false

    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
    id("org.ajoberstar.grgit") version "5.0.+" apply false
    id("com.gradleup.nmcp.aggregation") version "1.4.3"

    id("dev.isxander.secrets") version "0.1.0"
}
stonecutter active file("versions/current")

allprojects {
    repositories {
        exclusiveContent {
            forRepository { maven("https://maven.terraformersmc.com") }
            filter { includeGroup("com.terraformersmc") }
        }
        exclusiveContent {
            forRepositories(
                maven("https://maven.isxander.dev/releases"),
                maven("https://maven.isxander.dev/snapshots"),
            )
            filter { includeGroup("dev.isxander") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.quiltmc.org/repository/release") }
            filter { includeGroupAndSubgroups("org.quiltmc") }
        }
        exclusiveContent {
            forRepository { maven("https://thedarkcolour.github.io/KotlinForForge/") }
            filter { includeGroup("thedarkcolour") }
        }
        mavenCentral()
    }
}

stonecutter {
    parameters {
        constants {
            fun String.propDefined() = project(node.metadata.project).findProperty(this)?.toString()?.isNotBlank() ?: false

            put("controlify", "deps.controlify".propDefined())
            put("mod-menu", "deps.modMenu".propDefined())
        }
    }
}

version = property("modVersion") as String

tasks.register("clean") {
    group = "build"
    delete(layout.buildDirectory.dir("finalJars"))
}


nmcpAggregation {
    centralPortal {
        username = secrets.gradleProperty("mcentral.username")
        password = secrets.gradleProperty("mcentral.password")

        publicationName = "yet-another-config-lib:$version"
    }
}
dependencies {
    allprojects {
        nmcpAggregation(project(path))
    }
}

