package net.auoeke.fabricmodgradle.extension.contact

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.json

internal class Person(val name: String, val contact: Contact? = null) : JsonSerializable {
    override fun toJson(context: JsonSerializationContext): JsonElement {
        if (this.contact == null || this.contact.empty) {
            return this.name.json
        }

        return JsonObject().also {
            it.add("name", this.name.json)
            it.add("contact", context.serialize(this.contact))
        }
    }
}
