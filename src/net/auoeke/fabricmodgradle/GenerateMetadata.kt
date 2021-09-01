package net.auoeke.fabricmodgradle

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import net.auoeke.fabricmodgradle.extension.Metadata
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.extension.json.JsonSerializableAdapter
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import java.io.File
import java.io.StringWriter
import javax.inject.Inject

@Suppress("LeakingThis", "UNCHECKED_CAST")
open class GenerateMetadata @Inject constructor(private val set: SourceSet) : DefaultTask() {
    private val output: File = this.project.buildDir.resolve("generated/resources/${set.name}/fabric.mod.json")

    @OutputDirectory
    val outputDirectory: File = this.output.parentFile

    private val metadata: Metadata = Metadata(this.project, this.set, this.outputDirectory).also {
        this.project.extensions.add(this.set.getTaskName(null, "mod"), it)
    }

    init {
        this.group = "fabric"
        this.outputs.upToDateWhen {false}

        this.task<AbstractCopyTask>(this.set.processResourcesTaskName).also {
            it.dependsOn(this)
            this.finalizedBy(it)
        }

        this.project.afterEvaluate {
            arrayOf<Jar?>(this.task(this.set.jarTaskName), this.task(this.set.sourcesJarTaskName)).forEach {
                it?.dependsOn(this)
            }
        }
    }

    @TaskAction
    fun generate() {
        if (this.metadata.initialized == true) {
            val stringWriter = StringWriter()
            gson.toJson(this.metadata, Metadata::class.java, JsonWriter(stringWriter).apply {setIndent("    ")})
            this.output.writeText(stringWriter.toString())
        }
    }

    private fun <T : Task?> task(name: String): T = this.project.tasks.findByName(name) as T

    companion object {
        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeHierarchyAdapter(JsonSerializable::class.java, JsonSerializableAdapter())
            .create()
    }
}
