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

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FabricModGradle : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all {
            project.tasks.create(it.getTaskName("generate", "metadata"), GenerateMetadata::class.java, it)
        }
    }

    companion object {
        fun configure(project: Project, obj: Any, configurator: Closure<*>?): Any? {
            var result: Any? = null
            project.configure(obj, configurator?.andThen(closure {it: Any? -> result = it}))

            return result
        }
    }
}
