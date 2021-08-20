package net.auoeke.fabricmodgradle.extension.entrypoint

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

@CompileStatic
class EntrypointTarget implements JsonSerializable {
    String value
    String adapter

    @Override
    JsonElement toJson() {
        if (this.adapter === null) {
            return new JsonPrimitive(this.value)
        }

        var json = new JsonObject()
        json.add("value", new JsonPrimitive(this.value))
        json.add("adapter", new JsonPrimitive(this.adapter))

        return json
    }
}
