package net.auoeke.fabricmodgradle

import groovy.transform.CompileStatic
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test

@CompileStatic
class FabricModGradleTest {
    @Test
    void run() {
        GradleRunner.create().withProjectDir(new File("test/resources")).withPluginClasspath().withDebug(true).withArguments("--stacktrace").build()
    }
}
