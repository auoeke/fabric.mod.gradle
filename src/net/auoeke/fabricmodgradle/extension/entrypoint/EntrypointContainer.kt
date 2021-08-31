package net.auoeke.fabricmodgradle.extension.entrypoint

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport
import net.auoeke.fabricmodgradle.catch
import net.auoeke.fabricmodgradle.emptyOrNull
import net.auoeke.fabricmodgradle.extension.Metadata
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.string
import java.lang.reflect.Method

@Suppress("UNCHECKED_CAST")
class EntrypointContainer(private val metadata: Metadata) : GroovyObjectSupport(), JsonSerializable, Container {
    var entrypoints: MutableMap<String, MutableList<EntrypointTarget>>? = null
    override val empty: Boolean get() = this.entrypoints.emptyOrNull

    fun add(entrypoint: String, target: EntrypointTarget) {
        (this.entrypoints ?: LinkedHashMap<String, MutableList<EntrypointTarget>>().also {this.entrypoints = it}).computeIfAbsent(entrypoint) {ArrayList()} += target
    }

    fun add(entrypoint: String, configuration: Closure<*>) = this.add(entrypoint, EntrypointTarget().also {this.metadata.project.configure(it, configuration)})
    fun add(entrypoint: String, adapter: String?, vararg targets: String) = this.add(entrypoint, EntrypointTarget(targets.toList(), adapter))
    fun add(entrypoint: String, type: String) = this.add(entrypoint, null, type)

    fun add(entrypoint: String, targets: Map<String, Array<String>>) {
        targets.forEach {target ->
            this.add(entrypoint, target.key, *target.value)
        }
    }

    override fun invokeMethod(name: String, args: Any): Any? {
        val argArray = (args as Array<*>).map {it.string}.toTypedArray()
        val argList = Array<String?>(argArray.size + 1) {null}.also {
            argArray.copyInto(it, 1, endIndex = argArray.size)
            it[0] = name
        }

        methods.forEach {method ->
            catch<IllegalArgumentException> {
                return method(this, *argList)
            }
        }

        return null
    }

    override fun toJson(): JsonElement = JsonObject().also {json ->
        this.entrypoints!!.forEach {(entrypoint, targets) ->
            json.add(entrypoint, JsonArray().also {array ->
                targets.forEach {target ->
                    target.toJson().forEach(array::add)
                }
            })
        }
    }

    companion object {
        private val methods: List<Method> = EntrypointContainer::class.java.declaredMethods.filter {it.name == "add"}
    }
}
