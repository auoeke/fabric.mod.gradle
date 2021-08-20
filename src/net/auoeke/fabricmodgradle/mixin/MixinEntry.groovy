package net.auoeke.fabricmodgradle.mixin

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class MixinEntry implements JsonSerializable {
    String config
    String environment

    @Override
    JsonElement toJson() {
        if (this.environment == null) {
            return new JsonPrimitive(this.config)
        }

        var json = new JsonObject()
        json.add("config", new JsonPrimitive(this.config))
        json.add("environment", new JsonPrimitive(this.environment))

        return json
    }
}
