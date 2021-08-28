package net.auoeke.fabricmodgradle.extension.entrypoint

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.string

@Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST")
class EntrypointTarget(var value: List<String>? = null, var adapter: String? = null) : JsonSerializable {
    @Override
    override fun toJson(): JsonElement {
        val value = if (this.value!!.size == 1) JsonPrimitive(this.value!![0]) else JsonArray().also {
            this.value!!.forEach(it::add)
        }

        if (this.adapter === null) {
            return value
        }

        val json = JsonObject()
        json.add("adapter", JsonPrimitive(this.adapter))
        json.add("value", value)

        return json
    }

    constructor(value: String, adapter: String? = null) : this(listOf(value), adapter)
    constructor(map: Map<String, *>) : this(map["value"].let {value -> if (value is List<*>) value.map {it.string} else listOf(value.string)}, map["adapter"].string)
}
