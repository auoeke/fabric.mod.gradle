package net.auoeke.fabricmodgradle

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.extension.Extension
import net.auoeke.fabricmodgradle.extension.Metadata
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.extension.json.JsonSerializableAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Files

@CompileStatic
class FabricModGradle implements Plugin<Project> {
    public static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .registerTypeHierarchyAdapter(JsonSerializable, new JsonSerializableAdapter())
        .create()

    static Object configure(Project project, Object object, Closure configurator) {
        var result = null
        project.configure(object, configurator >> {result = it})

        return result
    }

    @Override
    void apply(Project project) {
        var extension = new Extension(project)
        project.extensions.add("mod", extension)

        project.afterEvaluate {
            extension.metadata.each {metadata ->
                var output = project.buildDir.toPath().resolve("generated/resources/${metadata.key.name}/fabric.mod.json")
                metadata.key.resources.srcDir(output)
                Files.createDirectories(output.parent)

                var stringWriter = new StringWriter()
                var jsonWriter = new JsonWriter(stringWriter)
                jsonWriter.setIndent("    ")
                gson.toJson(metadata.value, Metadata, jsonWriter)
                Files.writeString(output, stringWriter.buffer)
            }
        }
    }
}
