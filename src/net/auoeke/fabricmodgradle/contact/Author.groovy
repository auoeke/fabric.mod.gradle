package net.auoeke.fabricmodgradle.contact

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class Author implements JsonSerializable {
    public final String name
    public final Contact contact

    Author(String name, Contact contact = null) {
        this.name = name
        this.contact = contact
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        if (this.contact == null) {
            return new JsonPrimitive(this.name)
        }

        var json = new JsonObject()
        json.add("name", new JsonPrimitive(this.name))

        context.serialize(this.contact).asJsonObject.entrySet().each {
            json.add(it.key, it.value)
        }

        return json
    }
}
