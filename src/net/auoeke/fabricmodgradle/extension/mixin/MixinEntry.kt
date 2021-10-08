package net.auoeke.fabricmodgradle.extension.mixin

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

class MixinEntry(private val path: String? = null, private val environment: String? = null) : JsonSerializable {
    override fun toJson(): JsonElement {
        return environment?.let {
            JsonObject().also {
                it.add("config", JsonPrimitive(path))
                it.add("environment", JsonPrimitive(environment))
            }
        } ?: JsonPrimitive(path)
    }
}
