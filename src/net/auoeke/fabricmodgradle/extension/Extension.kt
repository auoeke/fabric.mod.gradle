package net.auoeke.fabricmodgradle.extension

import com.google.gson.stream.JsonWriter
import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport
import net.auoeke.fabricmodgradle.FabricModGradle
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.JavaPluginExtension
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.StringWriter
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Extension(private val project: Project) : GroovyObjectSupport() {
    private val metadata: MutableMap<SourceSet, Metadata> = HashMap()

    override fun invokeMethod(name: String, args: Any): Any? {
        return this.project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.findByName(name)?.let {set ->
            this.metadata.computeIfAbsent(set) {
                Metadata(this.project, set, this).also {metadata ->
                    this.project.tasks.getByName(set.classesTaskName).doLast {
                        val output = project.buildDir.toPath().resolve("generated/resources/${set.name}/fabric.mod.json")
                        set.output.dir(output.parent.createDirectories())

                        val stringWriter = StringWriter()
                        val jsonWriter = JsonWriter(stringWriter)
                        jsonWriter.setIndent("    ")
                        FabricModGradle.gson.toJson(metadata, Metadata::class.java, jsonWriter)
                        output.writeText(stringWriter.buffer)
                    }
                }
            }.also {this.project.configure(it, (args as Array<*>)[0] as Closure<*>?)}
        }
    }
}
