plugins {
    java
    `maven-publish`
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "top.mrxiaom.sweet.messages"
version = "1.0.0"
val shadowGroup = "top.mrxiaom.sweet.messages.libs"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://s01.oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

val shadowLink = configurations.create("shadowLink")
@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    // compileOnly("org.spigotmc:spigot:1.20") // NMS

    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("net.kyori:adventure-api:4.20.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation("net.kyori:adventure-text-minimessage:4.20.0")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("top.mrxiaom:PluginBase:1.4.0")
    implementation("com.github.technicallycoded:FoliaLib:0.4.4")
    implementation(project(":nms"))
    for (proj in project.project(":nms").subprojects) {
        if (proj.name.startsWith("v")) {
            add("shadowLink", proj)
        }
    }
}

setupJava(8)
tasks {
    shadowJar {
        archiveClassifier.set("")
        configurations.add(shadowLink)
        mapOf(
            "org.intellij.lang.annotations" to "annotations.intellij",
            "org.jetbrains.annotations" to "annotations.jetbrains",
            "top.mrxiaom.pluginbase" to "base",
            "com.tcoded.folialib" to "folialib",
            "net.kyori" to "kyori",
        ).forEach { (original, target) ->
            relocate(original, "$shadowGroup.$target")
        }
    }
    build {
        dependsOn(shadowJar)
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to version))
            include("plugin.yml")
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()
        }
    }
}
