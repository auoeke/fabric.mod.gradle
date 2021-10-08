package net.auoeke.fabricmodgradle.extension.misc

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

class LicenseContainer : JsonSerializable, Container {
    val licenses: MutableList<String> = ArrayList()
    override val empty: Boolean get() = licenses.isEmpty()

    override fun toJson(context: JsonSerializationContext): JsonElement {
        return if (licenses.size == 1) JsonPrimitive(licenses[0]) else context.serialize(licenses)
    }
}
