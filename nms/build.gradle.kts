
allprojects {
    setupJava(8)
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.rosewooddev.io/repository/public/")
    }
    dependencies {
        "compileOnly"("net.kyori:adventure-api:4.17.0")
        "compileOnly"("net.kyori:adventure-platform-bukkit:4.3.4")
    }
}

dependencies {
    "compileOnly"("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    "implementation"("org.jetbrains:annotations:24.0.0")
    for (proj in subprojects) {
        if (!proj.name.startsWith("v")) {
            "implementation"(proj)
        }
        if (proj.name != "shared") {
            proj.dependencies.add("implementation", project(":nms:shared"))
        }
    }
}
