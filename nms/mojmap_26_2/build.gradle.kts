repositories {
    maven("https://repo.codemc.io/repository/nms/")
}

setupJava(25)
dependencies {
    compileOnly("org.spigotmc:spigot-api:26.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:26.2-R0.1-SNAPSHOT")
    compileOnly("com.mojang:datafixerupper:10.0.21")
    compileOnly("com.mojang:brigadier:1.3.10")
}
