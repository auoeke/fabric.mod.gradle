package net.auoeke.fabricmodgradle.extension.relation

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import java.util.ArrayList

class Versions : JsonSerializable {
    var versions: MutableList<String> = ArrayList()

    override fun toJson(context: JsonSerializationContext): JsonElement {
        return if (this.versions.size == 1) JsonPrimitive(this.versions[0]) else context.serialize(this.versions)
    }
}
