package net.auoeke.fabricmodgradle

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class JarContainer implements JsonSerializable {
    public final List<String> jars = []

    @Override
    JsonElement toJson() {
        var json = new JsonArray(jars.size())
        jars.each {
            var file = new JsonObject()
            file.add("file", new JsonPrimitive(it))
            json.add(file)
        }

        return json
    }
}
