package net.auoeke.fabricmodgradle.extension.contact

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

internal class Person(val name: String, val contact: Contact? = null) : JsonSerializable {
    override fun toJson(context: JsonSerializationContext): JsonElement {
        if (this.contact == null || this.contact.empty) {
            return JsonPrimitive(this.name)
        }

        val json = JsonObject()
        json.add("name", JsonPrimitive(this.name))
        json.add("contact", context.serialize(this.contact))

        return json
    }
}
