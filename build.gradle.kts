plugins {
    alias(libs.plugins.architectury.plugin)
    alias(libs.plugins.architectury.loom) apply false

    alias(libs.plugins.unified.publishing) apply false
    alias(libs.plugins.github.release)
    alias(libs.plugins.grgit)
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

val changelogText = file("changelogs/${project.version}.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "architectury-plugin")

    version = "2.5.0+1.19.4"
    group = "dev.isxander"

    pluginManager.withPlugin("base") {
        val base = the<BasePluginExtension>()

        base.archivesName.set("yet-another-config-lib")
    }

    ext["changelogText"] = changelogText

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

    pluginManager.withPlugin("publishing") {
        val publishing = the<PublishingExtension>()

        publishing.repositories {
            val username = "XANDER_MAVEN_USER".let { System.getenv(it) ?: findProperty(it) }?.toString()
            val password = "XANDER_MAVEN_PASS".let { System.getenv(it) ?: findProperty(it) }?.toString()
            if (username != null && password != null) {
                maven(url = "https://maven.isxander.dev/releases") {
                    name = "Releases"
                    credentials {
                        this.username = username
                        this.password = password
                    }
                }
                maven(url = "https://maven.isxander.dev/snapshots") {
                    name = "Snapshots"
                    credentials {
                        this.username = username
                        this.password = password
                    }
                }
            } else {
                println("Xander Maven credentials not satisfied.")
            }
        }
    }
}

githubRelease {
    token(findProperty("GITHUB_TOKEN")?.toString())

    val githubProject: String by rootProject
    val split = githubProject.split("/")
    owner(split[0])
    repo(split[1])
    tagName("${project.version}")
    targetCommitish(grgit.branch.current().name)
    body(changelogText)
    releaseAssets(
        { project(":fabric").tasks["remapJar"].outputs.files },
        { project(":fabric").tasks["remapSourcesJar"].outputs.files },
        { project(":forge").tasks["remapJar"].outputs.files },
        { project(":forge").tasks["remapSourcesJar"].outputs.files },
    )
}

tasks.register("releaseMod") {
    group = "mod"

    dependsOn("githubRelease")
}

tasks.register("buildAll") {
    group = "mod"

    dependsOn(project(":fabric").tasks["build"])
    dependsOn(project(":forge").tasks["build"])
}
