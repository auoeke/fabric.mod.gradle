package net.auoeke.fabricmodgradle.extension.contact

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport
import java.util.HashSet
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import org.gradle.api.Project

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
internal class PersonContainer(private val project: Project) : GroovyObjectSupport(), JsonSerializable, Container {
    val people: MutableSet<Person> = HashSet()
    override val empty: Boolean get() = this.people.isEmpty()

    fun add(name: String, configuration: Closure<*>? = null): Person {
        val person = Person(name, if (configuration == null) null else Contact())
        this.project.configure(person.contact, configuration)
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
