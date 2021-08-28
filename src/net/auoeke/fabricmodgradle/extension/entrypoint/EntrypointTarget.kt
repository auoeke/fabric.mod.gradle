package net.auoeke.fabricmodgradle.extension.entrypoint

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.json
import net.auoeke.fabricmodgradle.string

@Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "JoinDeclarationAndAssignment")
class EntrypointTarget() {
    var adapter: String? = null
    lateinit var value: List<String>

    fun toJson(): List<JsonElement> {
        if (this.adapter === null) {
            return if (this.value.size == 1) listOf(JsonPrimitive(this.value.first())) else this.value.map(::JsonPrimitive)
        }

        return ArrayList<JsonElement>().also {objects ->
            this.value.forEach {target ->
                objects.add(JsonObject().also {
                    it.add("adapter", this.adapter.json)
                    it.add("value", target.json)
                })
            }
        }
    }

    constructor(value: List<String>, adapter: String? = null) : this() {
        this.value = value
        this.adapter = adapter
    }

    constructor(value: String) : this(listOf(value))
    constructor(map: Map<String, *>) : this(map["value"].let {value -> if (value is List<*>) value.map {it.string} else listOf(value.string)}, map["adapter"].string)
}
