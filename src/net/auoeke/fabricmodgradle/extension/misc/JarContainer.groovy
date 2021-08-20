package net.auoeke.fabricmodgradle.extension.misc

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

@CompileStatic
class JarContainer implements JsonSerializable, Container {
    public final List<String> jars = []

    @Override
    JsonElement toJson() {
        var json = new JsonArray(this.jars.size())
        this.jars.each {
            var file = new JsonObject()
            file.add("file", new JsonPrimitive(it))
            json.add(file)
        }

        return json
    }

    @Override
    boolean isEmpty() {
        return this.jars.empty
    }
}
