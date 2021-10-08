package net.auoeke.fabricmodgradle.extension.mixin

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import net.auoeke.fabricmodgradle.empty
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import java.util.ArrayList

class MixinContainer : JsonSerializable, Container {
    private val entries: MutableList<MixinEntry> = ArrayList()
    override val empty: Boolean get() = entries.empty

    fun add(environment: String? = null, path: String) {
         entries += MixinEntry(path, environment)
    }

    fun client(configuration: String) {
        add("client", configuration)
    }

    fun server(configuration: String) {
        add("server", configuration)
    }

    override fun toJson(context: JsonSerializationContext): JsonElement = context.serialize(entries)
}
