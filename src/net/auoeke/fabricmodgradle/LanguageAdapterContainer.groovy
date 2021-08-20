package net.auoeke.fabricmodgradle

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.Container
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class LanguageAdapterContainer implements JsonSerializable, Container {
    private final Map<String, String> adapters = [:]

    @Override
    void setProperty(String propertyName, Object newValue) {
        this.adapters[propertyName] = newValue as String
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return context.serialize(this.adapters)
    }

    @Override
    boolean isEmpty() {
        return this.adapters.isEmpty()
    }
}
