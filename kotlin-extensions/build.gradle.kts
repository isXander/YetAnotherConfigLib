plugins {
    alias(libs.plugins.architectury.loom)
    kotlin("jvm") version libs.versions.kotlin.get()
}

architectury {
    val enabledLoaders = rootProject.properties["loaders"].toString().split(",").map { it.trim() }
    common(enabledLoaders)
}

loom {
    silentMojangMappingsLicense()
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        val qm = libs.versions.quilt.mappings.get()
        if (qm != "0")
            mappings("org.quiltmc:quilt-mappings:${libs.versions.minecraft.get()}+build.${libs.versions.quilt.mappings.get()}:intermediary-v2")
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)

    implementation(project(path = ":common", configuration = "namedElements"))
    implementation(kotlin("stdlib-jdk8"))
}

java {
    withSourcesJar()
}

tasks {
    remapJar {
        archiveClassifier.set(null as String?)

        from(rootProject.file("LICENSE"))
    }
}

publishing {
    publications {
        create<MavenPublication>("kotlin-extensions") {
            groupId = "dev.isxander.yacl"
            artifactId = "yet-another-config-lib-kotlin"

            from(components["java"])
        }
    }
}
tasks.findByPath("publishCommonPublicationToReleasesRepository")?.let {
    rootProject.tasks["releaseMod"].dependsOn(it)
}
