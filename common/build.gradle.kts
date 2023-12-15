plugins {
    alias(libs.plugins.architectury.loom)
}

base {
    archivesName.set("yet-another-config-lib")
}

architectury {
    val enabledLoaders = rootProject.properties["loaders"].toString().split(",").map { it.trim() }
    common(enabledLoaders)
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath.set(file("src/main/resources/yacl.accesswidener"))
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment)
    })
    modImplementation(libs.fabric.loader)

    implementation(libs.bundles.twelvemonkeys.imageio)
    implementation(libs.bundles.quilt.parsers)
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
        create<MavenPublication>("common") {
            groupId = "dev.isxander.yacl"
            artifactId = "yet-another-config-lib-common"

            from(components["java"])
        }
    }
}
tasks.findByPath("publishCommonPublicationToReleasesRepository")?.let {
    rootProject.tasks["releaseMod"].dependsOn(it)
}
