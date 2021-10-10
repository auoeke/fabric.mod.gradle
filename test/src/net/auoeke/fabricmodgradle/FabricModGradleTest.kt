package net.auoeke.fabricmodgradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import java.io.File

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FabricModGradleTest {
    @BeforeAll
    fun clean() {
        run("clean")
    }

    @Test
    fun build() {
        run("build")
    }

   @Test
    fun run() {
        run("runClient")
    }

    private fun runner(vararg args: String) = GradleRunner.create().withProjectDir(File("test/project")).withDebug(true).withPluginClasspath().withArguments(listOf("--stacktrace") + args).forwardOutput()
    private fun run(vararg args: String) = runner(*args).build()
}
