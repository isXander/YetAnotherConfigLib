plugins {
    alias(libs.plugins.architectury.loom)
}

base {
    archivesName.set("yet-another-config-lib")
}

architectury {
    common("fabric", "forge")
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath.set(file("src/main/resources/yacl.accesswidener"))
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        mappings("org.quiltmc:quilt-mappings:${libs.versions.minecraft.get()}+build.${libs.versions.quilt.mappings.get()}:intermediary-v2")
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("common") {
            groupId = "dev.isxander.yacl"
            artifactId = "yet-another-config-lib-common"

            from(components["java"])
            artifact(tasks.remapSourcesJar.get())
        }
    }
}
tasks.findByPath("publishCommonPublicationToReleasesRepository")?.let {
    rootProject.tasks["releaseMod"].dependsOn(it)
}
