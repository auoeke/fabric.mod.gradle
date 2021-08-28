package net.auoeke.fabricmodgradle.extension.json

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class JsonSerializableAdapter : JsonSerializer<JsonSerializable> {
    override fun serialize(source: JsonSerializable, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = source.toJson(context)
}
