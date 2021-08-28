package net.auoeke.fabricmodgradle.extension.misc

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import net.auoeke.fabricmodgradle.empty
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

class IconContainer : JsonSerializable, Container {
    override val empty: Boolean get() = this.icons.empty
    var icons: MutableMap<Int?, String> = LinkedHashMap()

    override fun toJson(context: JsonSerializationContext): JsonElement {
        return if (this.icons.size == 1 && this.icons.contains(null)) JsonPrimitive(this.icons[null]) else context.serialize(this.icons)
    }
}
