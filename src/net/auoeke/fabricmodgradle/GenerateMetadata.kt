package net.auoeke.fabricmodgradle

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import net.auoeke.fabricmodgradle.extension.Metadata
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.extension.json.JsonSerializableAdapter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.StringWriter
import javax.inject.Inject

@Suppress("LeakingThis", "unused")
open class GenerateMetadata @Inject constructor(private val set: SourceSet) : DefaultTask() {
    private val output: File = this.project.buildDir.resolve("generated/resources/${set.name}/fabric.mod.json")

    @OutputDirectory
    val outputDirectory: File = this.output.parentFile

    private val metadata: Metadata = Metadata(this.project, this.set).also {
        this.project.extensions.add(this.set.getTaskName(null, "mod"), it)
    }

    init {
        this.group = "fabric"
        this.outputs.upToDateWhen {false}

        (this.project.tasks.getByName(this.set.processResourcesTaskName) as AbstractCopyTask).from(this)
    }

    @TaskAction
    fun generate() {
        if (this.metadata.initialized == true) {
            // this.set.output.dir(this.outputDirectory.apply {mkdirs()})
            val stringWriter = StringWriter()
            gson.toJson(this.metadata, Metadata::class.java, JsonWriter(stringWriter).apply {setIndent("    ")})
            this.output.writeText(stringWriter.toString())
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
