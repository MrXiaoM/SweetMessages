plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "9.3.0"
    id("com.github.gmazzo.buildconfig") version "5.6.7"
}

buildscript {
    repositories.mavenCentral()
    dependencies.classpath("top.mrxiaom:LibrariesResolver-Gradle:1.7.12")
}
val base = top.mrxiaom.gradle.LibraryHelper(project)

group = "top.mrxiaom.sweet.messages"
version = "1.0.4"
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

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("org.inventivetalent:bossbarapi:2.4.3-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.0")

    base.library("org.slf4j:slf4j-api:2.0.16")
    base.library("com.zaxxer:HikariCP:4.0.3")
    base.library("net.kyori:adventure-api:4.23.0")
    base.library("net.kyori:adventure-platform-bukkit:4.4.0")
    base.library("net.kyori:adventure-text-minimessage:4.23.0")
    base.library("net.kyori:adventure-text-serializer-plain:4.23.0")

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
java {
    disableAutoTargetJvm()
    withSourcesJar()
    withJavadocJar()
}
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
    val copyTask = create<Copy>("copyBuildArtifact") {
        dependsOn(shadowJar)
        from(shadowJar.get().outputs)
        rename { "${project.name}-$version.jar" }
        into(rootProject.file("out"))
    }
    build {
        dependsOn(copyTask)
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to version))
            include("plugin.yml")
        }
    }
    javadoc {
        (options as? StandardJavadocDocletOptions)?.apply {
            locale("zh_CN")
            charset("UTF-8")
            encoding("UTF-8")
            docEncoding("UTF-8")
            addBooleanOption("keywords", true)
            addBooleanOption("Xdoclint:none", true)

            val currentJavaVersion = JavaVersion.current()
            if (currentJavaVersion > JavaVersion.VERSION_1_9) {
                addBooleanOption("html5", true)
            }
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()

            artifact(tasks["shadowJar"]).classifier = null
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
}
