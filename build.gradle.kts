import top.mrxiaom.gradle.LibraryHelper

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "9.3.0"
    id("com.github.gmazzo.buildconfig") version "5.6.7"
}

buildscript {
    repositories.mavenCentral()
    dependencies.classpath("top.mrxiaom:LibrariesResolver-Gradle:1.7.27")
}
val base = LibraryHelper(project)

group = "top.mrxiaom.sweet.messages"
version = "1.0.6"
val pluginBaseModules = base.modules.run { listOf(library, l10n, actions, misc) }
val shadowGroup = "top.mrxiaom.sweet.messages.libs"

repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.inventivetalent.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
}

val shadowLink = configurations.create("shadowLink")
@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    // compileOnly("org.spigotmc:spigot:1.20") // NMS

    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("org.inventivetalent:bossbarapi:2.4.3-SNAPSHOT")
    compileOnly(base.depend.annotations)

    base.library("org.slf4j:slf4j-api:2.0.16")
    base.library("com.zaxxer:HikariCP:4.0.3")
    base.library(LibraryHelper.adventure("4.25.0"))
    base.collectPluginHolders()

    for (artifact in pluginBaseModules) {
        implementation(artifact)
    }
    implementation(base.resolver.lite)
    implementation("com.github.technicallycoded:FoliaLib:0.4.4") { isTransitive = false }
    implementation(project(":nms"))
    implementation(project(":nms:shared"))
    for (proj in project.project(":nms").subprojects) {
        if (proj.name.startsWith("v")) {
            add("shadowLink", proj)
        }
    }
}

buildConfig {
    className("BuildConstants")
    packageName("top.mrxiaom.sweet.messages")

    base.doResolveLibraries()
    buildConfigField("String", "VERSION", "\"${project.version}\"")
    buildConfigField("java.time.Instant", "BUILD_TIME", "java.time.Instant.ofEpochSecond(${System.currentTimeMillis() / 1000L}L)")
    buildConfigField("String[]", "RESOLVED_LIBRARIES", base.join())
}

setupJava(8)

LibraryHelper.initJava(project, base, 8, true)
LibraryHelper.initPublishing(project)

tasks {
    shadowJar {
        configurations.add(project.configurations.runtimeClasspath.get())
        configurations.add(shadowLink)
        mapOf(
            "top.mrxiaom.pluginbase" to "base",
            "com.tcoded.folialib" to "folialib",
        ).forEach { (original, target) ->
            relocate(original, "$shadowGroup.$target")
        }
    }
}
