package net.auoeke.fabricmodgradle

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.contact.Person
import net.auoeke.fabricmodgradle.contact.PersonContainer
import net.auoeke.fabricmodgradle.contact.Contact
import net.auoeke.fabricmodgradle.entrypoint.EntrypointContainer
import net.auoeke.fabricmodgradle.json.Container
import net.auoeke.fabricmodgradle.json.JsonSerializable
import net.auoeke.fabricmodgradle.mixin.MixinContainer
import net.auoeke.fabricmodgradle.relation.RelationContainer
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.stream.Stream

@SuppressWarnings("unused")
@CompileStatic
class Extension implements JsonSerializable {
    private static final List<String> environmentValues = [null, "client", "server", "*"]
    private static final List<Field> serializableFields = Stream.of(Extension.declaredFields).filter(field -> !(field.modifiers & (Modifier.STATIC | Modifier.TRANSIENT))).toList()

    public transient final String name

    public int schemaVersion = 1
    public String id
    public String version

    public String modName
    public String description

    public Contact contact = new Contact()

    public PersonContainer authors
    public PersonContainer contributors

    public String environment
    public EntrypointContainer entrypoints

    public LanguageAdapterContainer languageAdapters = new LanguageAdapterContainer()

    public MixinContainer mixins = new MixinContainer()

    public RelationContainer depends = new RelationContainer(),
                             recommends = new RelationContainer(),
                             suggests = new RelationContainer(),
                             breaks = new RelationContainer(),
                             conflicts = new RelationContainer()

    public JarContainer jars = new JarContainer()

    private transient final Project project

    Extension(Project project, String name) {
        this.project = project
        this.authors = new PersonContainer(project)
        this.contributors = new PersonContainer(project)
        this.entrypoints = new EntrypointContainer(project)
        this.name = name

        this.id = project.getName()
        this.setVersion(project.getVersion())
        this.description = project.description
    }

    SourceSet getSet() {
        return this.project.extensions.getByType(JavaPluginExtension).sourceSets.getByName(this.name)
    }

    void setVersion(Object version) {
        this.version = version as String
    }

    void setName(String name) {
        this.modName = name
    }

    void setEnvironment(String environment) {
        if (!environmentValues.contains(environment)) {
            throw new IllegalArgumentException("environment must be one of ${environmentValues} but ${environment} was supplied.")
        }

        this.environment = environment
    }

    void entrypoints(Closure configuration) {
        this.configure(this.entrypoints, configuration)
    }

    void jars(String... paths) {
        this.jars.jars.addAll(paths)
    }

    void jar(String path) {
        this.jars.jars.add(path)
    }

    void languageAdapters(Closure configuration) {
        this.configure(this.languageAdapters, configuration)
    }

    void languageAdapter(String key, String type) {
        this.languageAdapters[key] = type
    }

    void mixins(Closure configuration) {
        this.configure(this.mixins, configuration)
    }

    void mixin(String configuration) {
        this.mixin(null, configuration)
    }

    void mixins(String... configurations) {
        configurations.each {this.mixin(it)}
    }

    void mixin(String environment, String configuration) {
        this.mixins.add(environment, configuration)
    }

    void depends(Closure configuration) {
        this.configure(this.depends, configuration)
    }

    void recommends(Closure configuration) {
        this.configure(this.recommends, configuration)
    }

    void suggests(Closure configuration) {
        this.configure(this.suggests, configuration)
    }

    void breaks(Closure configuration) {
        this.configure(this.breaks, configuration)
    }

    void conflicts(Closure configuration) {
        this.configure(this.conflicts, configuration)
    }

    void contact(Closure configuration) {
        this.configure(this.contact, configuration)
    }

    void authors(Closure configuration) {
        this.configure(this.authors, configuration)
    }

    void author(String author, Closure configuration) {
        this.authors.add(author, configuration)
    }

    void author(String author) {
        this.authors.people.add(new Person(author))
    }

    void authors(String... authors) {
        authors.each {this.author(it)}
    }

    void contributors(Closure configuration) {
        this.configure(this.contributors, configuration)
    }

    void contributor(String author, Closure configuration) {
        this.contributors.add(author, configuration)
    }

    void contributor(String contributor) {
        this.contributors.people.add(new Person(contributor))
    }

    void contributors(String... contributors) {
        contributors.each {this.contributor(it)}
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        var json = new JsonObject()

        serializableFields.each {field ->
            field.trySetAccessible()
            var value = field.get(this)

            if (value != null && !(value instanceof Container && (value as Container).empty || value instanceof Collection && (value as Collection).empty)) {
                json.add(field.name, context.serialize(value))
            }
        }

        return json
    }

    private void configure(Object object, Closure configuration) {
        this.project.configure(object, configuration)
    }
}
