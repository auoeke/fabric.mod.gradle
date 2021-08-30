package net.auoeke.fabricmodgradle

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import groovy.lang.Closure
import net.auoeke.fabricmodgradle.extension.Metadata
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.extension.json.JsonSerializableAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import java.io.StringWriter
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FabricModGradle : Plugin<Project> {
    private val metadata: MutableMap<SourceSet, Metadata> = HashMap()

    override fun apply(project: Project) {
        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.also {
            it.all {set ->
                set.extensions.add("mod", Metadata(project, set).also {metadata ->
                    this.metadata[set] = metadata

                    project.tasks.getByName(set.classesTaskName).doLast {
                        if (metadata.initialized == true) {
                            val output = project.buildDir.toPath().resolve("generated/resources/${set.name}/fabric.mod.json")
                            set.output.dir(output.parent.createDirectories())

                            val stringWriter = StringWriter()
                            val jsonWriter = JsonWriter(stringWriter)
                            jsonWriter.setIndent("    ")
                            gson.toJson(metadata, Metadata::class.java, jsonWriter)
                            output.writeText(stringWriter.buffer)
                        }
                    }
                })
            }
        }.also {project.extensions.add("mod", it.getByName("main").extensions.getByName("mod"))}
    }

    companion object {
        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeHierarchyAdapter(JsonSerializable::class.java, JsonSerializableAdapter())
            .create()

        fun configure(project: Project, obj: Any, configurator: Closure<*>?): Any? {
            var result: Any? = null
            project.configure(obj, configurator?.andThen(closure {it: Any? -> result = it}))

            return result
        }
    }
}
