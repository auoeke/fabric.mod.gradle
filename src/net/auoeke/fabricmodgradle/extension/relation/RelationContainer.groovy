package net.auoeke.fabricmodgradle.extension.relation

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

@CompileStatic
class RelationContainer implements JsonSerializable, Container {
    public final Map<String, Versions> relations = [:]

    void add(Map<String, ?> entries) {
        var versions = new Versions()

        entries.each {entry ->
            versions.versions = entry.value.class.isArray() || entry.value instanceof Collection ? entry.value as List<String> : [entry.value as String]
            relations[entry.key] = versions
        }
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return context.serialize(this.relations)
    }

    @Override
    boolean isEmpty() {
        return this.relations.isEmpty()
    }
}
