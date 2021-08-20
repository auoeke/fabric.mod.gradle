package net.auoeke.fabricmodgradle.relation

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.Container
import net.auoeke.fabricmodgradle.json.JsonSerializable

@CompileStatic
class RelationContainer implements JsonSerializable, Container {
    public final Map<String, Versions> relations = [:]

    @Override
    void setProperty(String propertyName, Object newValue) {
        this.relations[propertyName] = new Versions().with(true) {
            versions = newValue.class.isArray() || newValue instanceof Collection ? newValue as List<String> : [newValue as String]

            return it
        }
    }

    @Override
    Object invokeMethod(String name, Object args) {
        var array = args as Object[]
        var flattenedArguments = []

        array.each {argument ->
            argument.class.array ? flattenedArguments.addAll(argument) : flattenedArguments << argument
        }

        this.setProperty(name, flattenedArguments.toArray())

        return null
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return context.serialize(this.relations)
    }

    @Override
    boolean isEmpty() {
        return this.relations.isEmpty()
    }
}
