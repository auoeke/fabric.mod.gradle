package net.auoeke.fabricmodgradle.extension.misc

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.lang.GroovyObjectSupport
import net.auoeke.fabricmodgradle.empty
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.string

class LanguageAdapterContainer : GroovyObjectSupport(), JsonSerializable, Container {
    override val empty: Boolean get() = this.adapters.empty
    private val adapters: MutableMap<String, String> = LinkedHashMap()

    override fun setProperty(propertyName: String, newValue: Any) {
        this.adapters[propertyName] = newValue.string
    }

    override fun toJson(context: JsonSerializationContext): JsonElement {
        return context.serialize(this.adapters)
    }
}
