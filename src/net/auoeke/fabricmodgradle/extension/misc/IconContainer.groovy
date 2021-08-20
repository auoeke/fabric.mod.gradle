package net.auoeke.fabricmodgradle.extension.misc

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

@CompileStatic
class IconContainer implements JsonSerializable, Container {
    public Map<Integer, String> icons = [:]

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return this.icons.size() === 1 && this.icons.containsKey(null) ? new JsonPrimitive(this.icons.get(null)) : context.serialize(this.icons)
    }

    @Override
    boolean isEmpty() {
        return this.icons.isEmpty()
    }
}
