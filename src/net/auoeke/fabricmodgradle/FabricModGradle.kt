package net.auoeke.fabricmodgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

@Suppress("unused")
class FabricModGradle : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all {
            project.tasks.create(it.getTaskName("generate", "metadata"), GenerateMetadata::class.java, it)
        }
    }
}
