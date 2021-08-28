package net.auoeke.fabricmodgradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import java.io.File

class FabricModGradleTest {
    @Test
    fun run() {
        GradleRunner.create().withProjectDir(File("test/project")).withDebug(true).withPluginClasspath().withArguments("--stacktrace", "classes", "emptyClasses", "comprehensiveClasses").build()
    }
}
