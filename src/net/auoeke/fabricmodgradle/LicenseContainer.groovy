package net.auoeke.fabricmodgradle

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.Container
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class LicenseContainer implements JsonSerializable, Container {
    public final List<String> licenses = []

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return this.licenses.size() == 1 ? new JsonPrimitive(this.licenses[0]) : context.serialize(this.licenses)
    }

    @Override
    boolean isEmpty() {
        return this.licenses.empty
    }
}
