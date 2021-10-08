package net.auoeke.fabricmodgradle.extension.misc

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.auoeke.fabricmodgradle.empty
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

class JarContainer : JsonSerializable, Container {
    val jars: MutableList<String> = ArrayList()
    override val empty: Boolean get() = jars.empty

    override fun toJson(): JsonElement {
        return JsonArray(jars.size).also {json ->
            jars.forEach {
                val file = JsonObject()
                file.add("file", JsonPrimitive(it))
                json.add(file)
            }
        }
    }
}
