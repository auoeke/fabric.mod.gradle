package net.auoeke.fabricmodgradle.extension.json

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext

interface JsonSerializable {
    fun toJson(context: JsonSerializationContext): JsonElement {
        return this.toJson()
    }

    fun toJson(): JsonElement {
        TODO("implement this")
    }
}
