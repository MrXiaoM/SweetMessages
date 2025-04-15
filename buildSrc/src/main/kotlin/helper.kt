import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

fun Project.setupJava(ver: Int) {
    if (!plugins.hasPlugin("java")) {
        apply(plugin="java")
    }
    extensions.configure(org.gradle.api.plugins.JavaPluginExtension::class) {
        val javaVersion = JavaVersion.toVersion(ver)
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(ver))
        }
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (ver >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(ver)
        }
    }
}
