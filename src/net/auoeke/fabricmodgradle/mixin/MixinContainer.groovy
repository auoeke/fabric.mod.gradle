package net.auoeke.fabricmodgradle.mixin

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.Container
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class MixinContainer implements JsonSerializable, Container {
    public final List<MixinEntry> entries = []

    void add(String environment, String configuration) {
        this.entries.add(new MixinEntry(environment: environment, config: configuration))
    }

    void client(String configuration) {
        this.add("client", configuration)
    }

    void server(String configuration) {
        this.add("server", configuration)
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return context.serialize(this.entries)
    }

    @Override
    boolean isEmpty() {
        return this.entries.empty
    }
}
