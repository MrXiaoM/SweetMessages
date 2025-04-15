rootProject.name = "SweetMessages"

include(":nms")
for (file in File("nms").listFiles() ?: arrayOf()) {
    if (File(file, "build.gradle.kts").exists()) {
        include(":nms:${file.name}")
    }
}
