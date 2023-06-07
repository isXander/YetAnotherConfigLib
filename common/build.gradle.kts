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

    accessWidenerPath.set(file("src/main/resources/yacl3.accesswidener"))

    // Including YACL 2.x overwrites the identically named refmap, breaking all the mixins.
    // So we need to use a different name for the refmap.
    mixin {
        useLegacyMixinAp.set(true)
        defaultRefmapName.set("yacl3-refmap.json")
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        mappings("org.quiltmc:quilt-mappings:${libs.versions.minecraft.get()}+build.${libs.versions.quilt.mappings.get()}:intermediary-v2")
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)

    implementation(libs.twelvemonkeys.imageio.core)
    implementation(libs.twelvemonkeys.imageio.webp)
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
