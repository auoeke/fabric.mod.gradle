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
import java.io.StringWriter

@Suppress("unused")
class FabricModGradle : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all {set ->
            val output = project.buildDir.resolve("generated/resources/${set.name}/fabric.mod.json")
            val outputDirectory = output.parentFile.apply {mkdirs()}
            val metadata = Metadata(project, set, outputDirectory).also {
                project.extensions.add(set.getTaskName(null, "mod"), it)
            }

            project.gradle.projectsEvaluated {
                if (metadata.initialized == true) {
                    val stringWriter = StringWriter()
                    gson.toJson(metadata, Metadata::class.java, JsonWriter(stringWriter).apply {setIndent("    ")})
                    output.writeText(stringWriter.toString())
                }
            }
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
