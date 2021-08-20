package net.auoeke.fabricmodgradle

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class LanguageAdapterContainer implements JsonSerializable {
    private final Map<String, String> adapters = [:]

    @Override
    void setProperty(String propertyName, Object newValue) {
        this.adapters[propertyName] = String.valueOf(newValue)
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return context.serialize(this.adapters)
    }
}
