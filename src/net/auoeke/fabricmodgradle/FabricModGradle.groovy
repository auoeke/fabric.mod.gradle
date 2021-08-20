package net.auoeke.fabricmodgradle

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.JsonSerializable
import net.auoeke.fabricmodgradle.json.JsonSerializableAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Files

@CompileStatic
class FabricModGradle implements Plugin<Project> {
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().registerTypeHierarchyAdapter(JsonSerializable, new JsonSerializableAdapter()).create()

    @Override
    void apply(Project project) {
        var extension = project.container(Extension) {String name -> new Extension(project, name)}
        project.extensions.add("mod", extension)

        project.afterEvaluate {
            extension.all {Extension setExtension ->
                var output = project.buildDir.toPath().resolve("generated/resources/${setExtension.name}/fabric.mod.json")
                setExtension.set.resources.srcDir(output)
                Files.createDirectories(output.parent)

                var stringWriter = new StringWriter()
                var jsonWriter = new JsonWriter(stringWriter)
                jsonWriter.setIndent("    ")
                gson.toJson(setExtension, setExtension.class, jsonWriter)
                Files.writeString(output, stringWriter.buffer)
            }
        }
    }
}
