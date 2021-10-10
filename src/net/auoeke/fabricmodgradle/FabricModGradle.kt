package net.auoeke.fabricmodgradle

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import net.auoeke.fabricmodgradle.extension.Metadata
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.extension.json.JsonSerializableAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import java.io.File
import java.io.StringWriter

@Suppress("unused")
class FabricModGradle : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all {set ->
            val outputDirectory = project.buildDir.resolve("generated/resources/${set.name}")
                .also(File::mkdirs)
                .also(set.resources::srcDir)
            val metadata = Metadata(project, set, outputDirectory)
                .also {project.extensions.add(set.getTaskName(null, "mod"), it)}

            // @formatter:off
            val write = {_: Any -> if (metadata.initialized == true) {
                val stringWriter = StringWriter()
                gson.toJson(metadata, Metadata::class.java, JsonWriter(stringWriter).apply {setIndent("    ")})
                outputDirectory.resolve("fabric.mod.json").writeText(stringWriter.toString())
            }}
            // @formatter:on

            project.gradle.projectsEvaluated(write)
            project.tasks.getByName("clean").doLast(write)
        }
    }

    companion object {
        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeHierarchyAdapter(JsonSerializable::class.java, JsonSerializableAdapter())
            .create()
    }
}
