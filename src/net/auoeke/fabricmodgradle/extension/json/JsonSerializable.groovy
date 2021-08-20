package net.auoeke.fabricmodgradle.extension.json

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic

@CompileStatic
interface JsonSerializable {
    default JsonElement toJson(JsonSerializationContext context) {
        return this.toJson()
    }

    default JsonElement toJson() {
        return null
    }
}
