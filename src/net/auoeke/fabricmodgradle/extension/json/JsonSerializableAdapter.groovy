package net.auoeke.fabricmodgradle.extension.json

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import groovy.transform.CompileStatic

import java.lang.reflect.Type

@CompileStatic
class JsonSerializableAdapter implements JsonSerializer<JsonSerializable> {
    @Override
    JsonElement serialize(JsonSerializable source, Type typeOfSrc, JsonSerializationContext context) {
        return source.toJson(context)
    }
}
