package net.auoeke.fabricmodgradle.extension.contact

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

@CompileStatic
class Person implements JsonSerializable {
    public final String name
    public final Contact contact

    Person(String name, Contact contact = null) {
        this.name = name
        this.contact = contact
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        if (this.contact === null || this.contact.empty) {
            return new JsonPrimitive(this.name)
        }

        var json = new JsonObject()
        json.add("name", new JsonPrimitive(this.name))
        json.add("contact", context.serialize(this.contact))

        return json
    }
}
