plugins {
    `minecraft-module`
}

architectury {
    common("fabric", "forge")
}

loom {
    accessWidenerPath.set(file("src/main/resources/yacl.accesswidener"))
}

dependencies {
    modImplementation(libs.fabric.loader) // used for @Environment annotations
}
