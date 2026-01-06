plugins {
    kotlin("jvm") version "2.2.21" apply false

    id("dev.kikugie.stonecutter")

    val modstitchVersion = "0.8.1"
    id("dev.isxander.modstitch.base") version modstitchVersion apply false

    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
    id("org.ajoberstar.grgit") version "5.0.+" apply false
    id("com.gradleup.nmcp.aggregation") version "1.4.3"
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
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
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
        username = providers.gradleProperty("centralUsername").orNull
        password = providers.gradleProperty("centralPassword").orNull

        publicationName = "yet-another-config-lib:$version"
    }
}
dependencies {
    allprojects {
        nmcpAggregation(project(path))
    }
}

