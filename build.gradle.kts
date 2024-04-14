plugins {
    `java-library`
    kotlin("jvm") version "1.9.22"

    id("dev.architectury.loom") version "1.6.+"

    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
    `maven-publish`
    id("org.ajoberstar.grgit") version "5.0.+"

    id("io.github.p03w.machete") version "2.+"
}

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val isNeoforge = loader == "neoforge"
val isForge = loader == "forge"
val isForgeLike = isNeoforge || isForge

val mcVersion = stonecutter.current.version
val mcDep = findProperty("fmj.mcDep")?.toString()

group = "dev.isxander"
val versionWithoutMC = "3.4.0"
version = "$versionWithoutMC+${stonecutter.current.project}"
val isAlpha = "alpha" in version.toString()
val isBeta = "beta" in version.toString()

base {
    archivesName.set(property("modName").toString())
}

stonecutter.expression {
    when (it) {
        "controlify" -> isPropDefined("deps.controlify")
        "mod-menu" -> isPropDefined("deps.modMenu")
        "fabric" -> isFabric
        "neoforge" -> isNeoforge
        "forge" -> isForge
        "!forge" -> !isForge
        "forge-like" -> isForgeLike
        else -> null
    }
}

val testmod by sourceSets.creating {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/yacl.accesswidener"))

    runConfigs.all {
        ideConfigGenerated(false)
        runDir("../../run")
    }
    runs {
        create("testmodClient") {
            client()
            name = "Testmod Client"
            source(testmod)
            ideConfigGenerated(true)
            runDir("../../run")
        }
    }

    if (isForge) {
        forge {
            convertAccessWideners.set(true)
            mixinConfigs("yacl.mixins.json")
        }
    }

    createRemapConfigurations(testmod)
}

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.isxander.dev/snapshots")
    maven("https://maven.quiltmc.org/repository/release")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") }
        filter { includeGroup("maven.modrinth") }
    }
    exclusiveContent {
        forRepository { maven("https://thedarkcolour.github.io/KotlinForForge/") }
        filter { includeGroup("thedarkcolour") }
    }

    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    fun Dependency?.jij(): Dependency? = include(this!!)

    minecraft("com.mojang:minecraft:${if (mcVersion.contains("beta")) "1.20.5-pre1" else mcVersion}")

    mappings(loom.layered {
        optionalProp("deps.quiltMappings") {
            mappings("org.quiltmc:quilt-mappings:$mcVersion+build.$it:intermediary-v2")
        }
        officialMojangMappings()
    })

    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${findProperty("deps.fabricLoader")}")

        val fapiVersion = property("deps.fabricApi").toString()
        listOf(
            "fabric-resource-loader-v0",
        ).forEach {
            modImplementation(fabricApi.module(it, fapiVersion))
        }
        modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:$fapiVersion")

        modImplementation("net.fabricmc:fabric-language-kotlin:${findProperty("deps.fabricLangKotlin")}")
    }
    if (isNeoforge) {
        "neoForge"("net.neoforged:neoforge:${findProperty("deps.neoforge")}")

        modImplementation("thedarkcolour:kotlinforforge-neoforge:${findProperty("deps.kotlinForForge")}")
    }
    if (isForge) {
        "forge"("net.minecraftforge:forge:${findProperty("deps.forge")}")

        modImplementation("thedarkcolour:kotlinforforge:${findProperty("deps.kotlinForForge")}")

        // enable when it's needed
//        val mixinExtras = findProperty("deps.mixinExtras")
//        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:$mixinExtras")!!)
//        api("io.github.llamalad7:mixinextras-forge:$mixinExtras").jij()
    }

    listOf(
        "imageio:imageio-core",
        "imageio:imageio-webp",
        "imageio:imageio-metadata",
        "common:common-lang",
        "common:common-io",
        "common:common-image"
    ).forEach {
        implementation("com.twelvemonkeys.$it:${findProperty("deps.imageio")}").jij()
    }

    listOf(
        "json",
        "gson"
    ).forEach {
        implementation("org.quiltmc.parsers:$it:${findProperty("deps.quiltParsers")}").jij()
    }

    "testmodImplementation"(sourceSets.main.get().output)
}

java {
    withSourcesJar()
    //withJavadocJar()
}

tasks {
    processResources {
        val props = mutableMapOf(
            "id" to findProperty("modId"),
            "group" to project.group,
            "name" to findProperty("modName"),
            "description" to findProperty("modDescription"),
            "version" to project.version,
            "github" to findProperty("githubProject"),
            "mc" to mcDep
        )
        optionalProp("fmj.yaclDep") {
            props["yacl"] = it
        }

        props.forEach(inputs::property)

        filesMatching("fabric.mod.json") { expand(props) }
        filesMatching("META-INF/mods.toml") { expand(props) }
    }

    val releaseMod by registering {
        group = "mod"

        dependsOn("publishMods")
        dependsOn("publish")
    }
}

machete {
    json.enabled.set(false)
}

publishMods {
    displayName.set("YetAnotherConfigLib $versionWithoutMC for MC $mcVersion")
    file.set(tasks.remapJar.get().archiveFile)
    changelog.set(
        rootProject.file("changelogs/${versionWithoutMC}.md")
            .takeIf { it.exists() }
            ?.readText()
            ?: "No changelog provided."
    )
    type.set(when {
        isAlpha -> ALPHA
        isBeta -> BETA
        else -> STABLE
    })
    modLoaders.add("fabric")

    // modrinth and curseforge use different formats for snapshots. this can be expressed globally
    val stableMCVersions = listOf(stonecutter.current.project)

    val modrinthId: String by project
    if (modrinthId.isNotBlank() && hasProperty("modrinth.token")) {
        modrinth {
            projectId.set(modrinthId)
            accessToken.set(findProperty("modrinth.token")?.toString())
            minecraftVersions.addAll(stableMCVersions)

            requires { slug.set("fabric-api") }
        }

        tasks.getByName("publishModrinth") {
            dependsOn("optimizeOutputsOfRemapJar")
        }
    }

    val curseforgeId: String by project
    if (curseforgeId.isNotBlank() && hasProperty("curseforge.token")) {
        curseforge {
            projectId.set(curseforgeId)
            accessToken.set(findProperty("curseforge.token")?.toString())
            minecraftVersions.addAll(stableMCVersions)

            requires { slug.set("fabric-api") }
        }

        tasks.getByName("publishCurseforge") {
            dependsOn("optimizeOutputsOfRemapJar")
        }
    }

    val githubProject: String by project
    if (githubProject.isNotBlank() && hasProperty("github.token")) {
        github {
            repository.set(githubProject)
            accessToken.set(findProperty("github.token")?.toString())
            //commitish.set(grgit.branch.current().name)
        }

        tasks.getByName("publishGithub") {
            dependsOn("optimizeOutputsOfRemapJar")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mod") {
            groupId = "dev.isxander"
            artifactId = "zoomify"

            from(components["java"])
        }
    }

    repositories {
        val username = "XANDER_MAVEN_USER".let { System.getenv(it) ?: findProperty(it) }?.toString()
        val password = "XANDER_MAVEN_PASS".let { System.getenv(it) ?: findProperty(it) }?.toString()
        if (username != null && password != null) {
            maven(url = "https://maven.isxander.dev/releases") {
                name = "XanderReleases"
                credentials {
                    this.username = username
                    this.password = password
                }
            }
            tasks.getByName("publishModPublicationToXanderReleasesRepository") {
                dependsOn("optimizeOutputsOfRemapJar")
            }
        } else {
            println("Xander Maven credentials not satisfied.")
        }
    }
}

tasks.getByName("generateMetadataFileForModPublication") {
    dependsOn("optimizeOutputsOfRemapJar")
}

fun <T> optionalProp(property: String, block: (String) -> T?) {
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)
}

fun isPropDefined(property: String): Boolean {
    return property(property)?.toString()?.isNotBlank() ?: false
}
