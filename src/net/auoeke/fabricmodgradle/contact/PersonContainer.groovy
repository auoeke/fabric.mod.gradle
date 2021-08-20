package net.auoeke.fabricmodgradle.contact

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.Container
import net.auoeke.fabricmodgradle.json.JsonSerializable
import org.gradle.api.Project

@CompileStatic
class PersonContainer implements JsonSerializable, Container {
    public final Set<Person> people = []

    private final Project project

    PersonContainer(Project project) {
        this.project = project
    }

    Person add(String name, Closure configuration = null) {
        var person = new Person(name, configuration == null ? null as Contact : new Contact())
        this.project.configure(person.contact, configuration)
        this.people.add(person)

        return person
    }

    @Override
    Object invokeMethod(String name, Object args) {
        return this.add(name, (args as Object[])[0] as Closure)
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return context.serialize(this.people)
    }

    @Override
    boolean isEmpty() {
        return this.people.empty
    }
}
