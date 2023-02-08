plugins {
    java

    id("fabric-loom") version "1.1.+" // 1.0.+ but patch must be 16 or higher
    id("io.github.juuxel.loom-quiltflower") version "1.8.+"

    id("com.modrinth.minotaur") version "2.6.+"
    id("me.hypherionmc.cursegradle") version "2.+"
    id("com.github.breadmoirai.github-release") version "2.+"
    `maven-publish`

    id("io.github.p03w.machete") version "1.+"
    id("org.ajoberstar.grgit") version "5.0.0"
}

val ciRun = System.getenv().containsKey("GITHUB_ACTIONS")

group = "dev.isxander"
version = "2.3.0+beta.2"

if (ciRun)
    version = "$version+${grgit.branch.current().name.replace('/', '.')}-SNAPSHOT"

loom {
    splitEnvironmentSourceSets()

    mods {
        register("yet-another-config-lib") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

val testmod by sourceSets.registering {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    compileClasspath += sourceSets["client"].compileClasspath
    runtimeClasspath += sourceSets["client"].runtimeClasspath
}

loom {
    accessWidenerPath.set(file("src/main/resources/yacl.accesswidener"))

    runs {
        register("testmod") {
            client()
            ideConfigGenerated(true)
            name("Test Mod")
            source(testmod.get())
        }

        named("client") { ideConfigGenerated(false) }
        named("server") { ideConfigGenerated(false) }
    }

    createRemapConfigurations(testmod.get())
}

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com")
    maven("https://maven.quiltmc.org/repository/release")
}

val minecraftVersion: String by project
val fabricLoaderVersion: String by project
val qmBuild: String by project

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        mappings("org.quiltmc:quilt-mappings:$minecraftVersion+build.$qmBuild:intermediary-v2")
        officialMojangMappings()
    })
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    "modClientImplementation"(fabricApi.module("fabric-resource-loader-v0", "0.73.1+1.19.4"))

    "testmodImplementation"(sourceSets.main.get().output)
    "testmodImplementation"(sourceSets["client"].output)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    processResources {
        val modId: String by project
        val modName: String by project
        val modDescription: String by project
        val githubProject: String by project

        inputs.property("id", modId)
        inputs.property("group", project.group)
        inputs.property("name", modName)
        inputs.property("description", modDescription)
        inputs.property("version", project.version)
        inputs.property("github", githubProject)

        filesMatching(listOf("fabric.mod.json", "quilt.mod.json")) {
            expand(
                "id" to modId,
                "group" to project.group,
                "name" to modName,
                "description" to modDescription,
                "version" to project.version,
                "github" to githubProject,
            )
        }
    }

    register("releaseMod") {
        group = "mod"

        dependsOn("modrinth")
        dependsOn("modrinthSyncBody")
        dependsOn("curseforge")
        dependsOn("publishModPublicationToReleasesRepository")
        dependsOn("githubRelease")
    }
}

val changelogText = file("changelogs/${project.version}.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

val modrinthId: String by project
if (modrinthId.isNotEmpty()) {
    modrinth {
        token.set(findProperty("MODRINTH_TOKEN")?.toString())
        projectId.set(modrinthId)
        versionNumber.set("${project.version}")
        versionType.set("release")
        uploadFile.set(tasks["remapJar"])
        gameVersions.set(listOf("1.19.3"))
        loaders.set(listOf("fabric", "quilt"))
        dependencies {
            required.project("fabric-api")
        }
        changelog.set(changelogText)
        syncBodyFrom.set(file("README.md").readText())
    }
}

val curseforgeId: String by project
if (hasProperty("CURSEFORGE_TOKEN") && curseforgeId.isNotEmpty()) {
    curseforge {
        apiKey = findProperty("CURSEFORGE_TOKEN")
        project(closureOf<me.hypherionmc.cursegradle.CurseProject> {
            mainArtifact(tasks["remapJar"], closureOf<me.hypherionmc.cursegradle.CurseArtifact> {
                displayName = "${project.version}"
            })

            id = curseforgeId
            releaseType = "release"
            addGameVersion("1.19.3")
            addGameVersion("Fabric")
            addGameVersion("Java 17")

            changelog = changelogText
            changelogType = "markdown"

            relations(closureOf<me.hypherionmc.cursegradle.CurseRelation> {
                requiredDependency("fabric-api")
            })
        })

        options(closureOf<me.hypherionmc.cursegradle.Options> {
            forgeGradleIntegration = false
        })
    }
}

githubRelease {
    token(findProperty("GITHUB_TOKEN")?.toString())

    val githubProject: String by project
    val split = githubProject.split("/")
    owner(split[0])
    repo(split[1])
    tagName("${project.version}")
    targetCommitish(grgit.branch.current().name)
    body(changelogText)
    releaseAssets(tasks["remapJar"].outputs.files)
}

publishing {
    publications {
        create<MavenPublication>("mod") {
            groupId = "dev.isxander"
            artifactId = "yet-another-config-lib"

            from(components["java"])
        }
    }

    repositories {
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
