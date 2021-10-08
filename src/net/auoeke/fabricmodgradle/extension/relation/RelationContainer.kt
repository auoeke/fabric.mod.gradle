package net.auoeke.fabricmodgradle.extension.relation

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import net.auoeke.fabricmodgradle.empty
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.string

@Suppress("UNCHECKED_CAST")
class RelationContainer : JsonSerializable, Container {
    private val relations: MutableMap<String, Versions> = LinkedHashMap()
    override val empty: Boolean get() = relations.empty

    fun add(entries: Map<String, *>) {
        val versions = Versions()

        entries.forEach {(key, value) ->
            versions.versions = when (value) {
                is Array<*> -> value.toMutableList() as MutableList<String>
                is Collection<*> -> value.toMutableList() as MutableList<String>
                else -> mutableListOf(value.string)
            }
            relations[key] = versions
        }
    }

    override fun toJson(context: JsonSerializationContext): JsonElement {
        return context.serialize(relations)
    }
}
