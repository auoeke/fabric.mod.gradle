package net.auoeke.fabricmodgradle.relation

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class Versions implements JsonSerializable {
    public List<String> versions = []

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return this.versions.size() == 1 ? new JsonPrimitive(this.versions[0]) : context.serialize(this.versions)
    }
}
