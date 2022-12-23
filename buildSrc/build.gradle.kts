plugins {
    `kotlin-dsl`
    kotlin("jvm") version embeddedKotlinVersion
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.fabricmc.net") { name = "FabricMC" }
    maven("https://maven.quiltmc.org/repository/release") { name = "QuiltMC" }
    maven("https://maven.architectury.dev") { name = "Architectury" }
    maven("https://maven.minecraftforge.net") { name = "Minecraft Forge" }
}

dependencies {
    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"

    implementation(plugin("architectury-plugin", "3.4.+"))
    implementation(plugin("dev.architectury.loom", "1.0.+"))
    implementation(plugin("io.github.juuxel.loom-quiltflower", "1.8.+"))
    implementation(plugin("com.github.johnrengelman.shadow", "7.1.+"))

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
