package net.auoeke.fabricmodgradle.extension.contact

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport
import net.auoeke.fabricmodgradle.extension.Metadata
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable

class PersonContainer(private val metadata: Metadata) : GroovyObjectSupport(), JsonSerializable, Container {
    val people: MutableSet<Person> = HashSet()
    override val empty: Boolean get() = this.people.isEmpty()

    fun add(name: String, configuration: Closure<*>? = null): Person {
        val person = Person(name, if (configuration == null) null else Contact())
        this.metadata.configure(person.contact, configuration)
        this.people.add(person)

        return person
    }

    override fun invokeMethod(name: String, args: Any): Any {
        return this.add(name, (args as Array<*>)[0] as Closure<*>)
    }

    override fun toJson(context: JsonSerializationContext): JsonElement {
        return context.serialize(this.people)
    }
}
