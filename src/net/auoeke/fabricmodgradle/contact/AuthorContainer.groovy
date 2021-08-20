package net.auoeke.fabricmodgradle.contact

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.Container
import net.auoeke.fabricmodgradle.json.JsonSerializable
import org.gradle.api.Project

@CompileStatic
class AuthorContainer implements JsonSerializable, Container {
    public final Set<Author> authors = []

    private final Project project

    AuthorContainer(Project project) {
        this.project = project
    }

    Author add(String name, Closure configuration = null) {
        var author = new Author(name, configuration == null ? null as Contact : new Contact())
        this.project.configure(author.contact, configuration)
        this.authors.add(author)

        return author
    }

    @Override
    Object invokeMethod(String name, Object args) {
        return this.add(name, (args as Object[])[0] as Closure)
    }

    @Override
    Object getProperty(String propertyName) {
        return this.add(propertyName)
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return context.serialize(this.authors)
    }

    @Override
    boolean isEmpty() {
        return this.authors.empty
    }
}
