package net.auoeke.fabricmodgradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import java.io.File

class FabricModGradleTest {
    @Test
    fun compile() {
        this.run("clean", "classes", "emptyClasses", "comprehensiveClasses")
    }

//    @Test
    fun run() {
        this.run("runClient")
    }

    private fun runner(vararg args: String) = GradleRunner.create().withProjectDir(File("test/project")).withDebug(true).withPluginClasspath().withArguments(listOf("--stacktrace") + args).forwardOutput()
    private fun run(vararg args: String) = this.runner(*args).build()
}
