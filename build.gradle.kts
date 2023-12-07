plugins {
    alias(libs.plugins.architectury.plugin)
    alias(libs.plugins.architectury.loom) apply false

    alias(libs.plugins.minotaur) apply false
    alias(libs.plugins.cursegradle) apply false
    alias(libs.plugins.github.release)
    alias(libs.plugins.grgit)
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

version = "3.3.1+1.20.4"

val isBeta = "beta" in version.toString()
val changelogText = rootProject.file("changelogs/${project.version}.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."
val snapshotVer = "${grgit.branch.current().name.replace('/', '.')}-SNAPSHOT"

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "architectury-plugin")

    version = rootProject.version
    group = "dev.isxander"

    if (System.getenv().containsKey("GITHUB_ACTIONS")) {
        version = "$version+$snapshotVer"
    }

    pluginManager.withPlugin("base") {
        val base = the<BasePluginExtension>()

        base.archivesName.set("yet-another-config-lib-${project.name}")
    }

    ext["changelogText"] = changelogText
    ext["isBeta"] = isBeta

    repositories {
        mavenCentral()
        maven("https://maven.isxander.dev/releases")
        maven("https://maven.isxander.dev/snapshots")
        maven("https://maven.quiltmc.org/repository/release")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.parchmentmc.org")
        maven("https://api.modrinth.com/maven") {
            name = "Modrinth"
            content {
                includeGroup("maven.modrinth")
            }
        }
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
    prerelease(isBeta)
    releaseAssets(
        { findProject(":fabric")?.tasks?.get("remapJar")?.outputs?.files },
        { findProject(":fabric")?.tasks?.get("remapSourcesJar")?.outputs?.files },
        { findProject(":forge")?.tasks?.get("remapJar")?.outputs?.files },
        { findProject(":forge")?.tasks?.get("remapSourcesJar")?.outputs?.files },
    )
}

tasks.register("releaseMod") {
    group = "mod"

    dependsOn("githubRelease")
}

tasks.register("buildAll") {
    group = "mod"

    findProject(":fabric")?.let { dependsOn(it.tasks["build"]) }
    findProject(":forge")?.let { dependsOn(it.tasks["build"]) }
}
