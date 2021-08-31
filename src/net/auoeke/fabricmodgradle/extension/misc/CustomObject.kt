package net.auoeke.fabricmodgradle.extension.misc

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport
import net.auoeke.fabricmodgradle.empty
import net.auoeke.fabricmodgradle.extension.Metadata
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CustomObject(private val metadata: Metadata) : GroovyObjectSupport(), JsonSerializable, Container {
    private var values: MutableMap<String, Any?> = LinkedHashMap()
    override val empty get() = this.values.empty

    override fun setProperty(propertyName: String, newValue: Any?) {
        this.values[propertyName] = newValue
    }

    override fun invokeMethod(name: String, args: Any): Any? {
        args as Array<*>

        when {
            args.size > 1 -> this.values[name] = args
            args.size == 1 -> {
                val arg = args[0]
                this.values[name] = when (arg) {
                    is Closure<*> -> CustomObject(this.metadata).also {it.configure(arg)}
                    else -> arg
                }
            }
        }

        return null
    }

    fun configure(configurator: Closure<*>?) {
        this.metadata.configure(this, configurator)
    }

    fun configure(values: MutableMap<String, Any?>) {
        if (!this.values.empty) {
            throw IllegalStateException("Custom values have already been set.")
        }

        this.values = values
    }

    override fun toJson(context: JsonSerializationContext): JsonElement = context.serialize(this.values)
}
