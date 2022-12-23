import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    id("architectury-plugin")
    id("dev.architectury.loom")
    id("io.github.juuxel.loom-quiltflower")
}

val libs = the<LibrariesForLibs>()

dependencies {
    minecraft(libs.minecraft)
    mappings("net.fabricmc:yarn:${libs.versions.minecraft.get()}+build.${libs.versions.yarn.get()}:v2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    withSourcesJar()
    withJavadocJar()
}

