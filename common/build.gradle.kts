plugins {
    alias(libs.plugins.architectury.loom)
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