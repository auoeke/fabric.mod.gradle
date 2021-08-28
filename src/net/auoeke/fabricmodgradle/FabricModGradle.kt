package net.auoeke.fabricmodgradle

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.lang.Closure
import net.auoeke.fabricmodgradle.extension.Extension
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.extension.json.JsonSerializableAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project

class FabricModGradle : Plugin<Project> {
    @Override
    override fun apply(project: Project) {
        project.extensions.add("mod", Extension(project))
    }

    companion object {
        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeHierarchyAdapter(JsonSerializable::class.java, JsonSerializableAdapter())
            .create()

        fun <V> configure(project: Project, obj: Any, configurator: Closure<V>): V? {
            var result: V? = null
            project.configure(obj, configurator.curry(closure {it: V? -> result = it}))

            return result
        }
    }
}
