package net.auoeke.fabricmodgradle.extension.entrypoint

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.auoeke.fabricmodgradle.json
import net.auoeke.fabricmodgradle.string

@Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "JoinDeclarationAndAssignment")
class EntrypointTarget() {
    var adapter: String? = null
    lateinit var value: List<String>

    fun toJson(): List<JsonElement> = when {
        this.adapter === null -> when (this.value.size) {
            1 -> listOf(this.value.first().json)
            else -> this.value.map(::JsonPrimitive)
        }
        else -> this.value.map {target ->
            JsonObject().also {
                it.add("adapter", this.adapter.json)
                it.add("value", target.json)
            }
        }
    }

    constructor(value: List<String>, adapter: String? = null) : this() {
        this.value = value
        this.adapter = adapter
    }

    constructor(value: String, adapter: String?) : this(listOf(value), adapter)
    constructor(value: String) : this(listOf(value))
    constructor(map: Map<*, *>) : this(map["value"].let {value -> if (value is List<*>) value.map {it.string} else listOf(value.string)}, map["adapter"].string)
}
