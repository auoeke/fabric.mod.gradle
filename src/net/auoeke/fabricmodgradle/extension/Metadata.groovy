package net.auoeke.fabricmodgradle.extension

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.FabricModGradle
import net.auoeke.fabricmodgradle.extension.contact.Contact
import net.auoeke.fabricmodgradle.extension.contact.Person
import net.auoeke.fabricmodgradle.extension.contact.PersonContainer
import net.auoeke.fabricmodgradle.extension.entrypoint.EntrypointContainer
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.extension.misc.IconContainer
import net.auoeke.fabricmodgradle.extension.misc.JarContainer
import net.auoeke.fabricmodgradle.extension.misc.LanguageAdapterContainer
import net.auoeke.fabricmodgradle.extension.misc.LicenseContainer
import net.auoeke.fabricmodgradle.extension.mixin.MixinContainer
import net.auoeke.fabricmodgradle.extension.relation.RelationContainer
import org.gradle.api.Project

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.stream.Stream

@SuppressWarnings("unused")
@CompileStatic
class Metadata implements JsonSerializable {
    private static final List<String> environmentValues = [null, "client", "server", "*"]
    private static final List<Field> serializableFields = Stream.of(Metadata.declaredFields).filter(field -> !(field.modifiers & (Modifier.STATIC | Modifier.TRANSIENT))).toList()

    public int schemaVersion = 1
    public String id
    public String version

    public String name
    public String description

    public Contact contact = new Contact()
    public PersonContainer authors
    public PersonContainer contributors

    public LicenseContainer license = new LicenseContainer()

    public IconContainer icon = new IconContainer()

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

    Metadata(Project project) {
        this.project = project

        this.id = project.getName()
        this.setVersion(project.getVersion())

        this.description = project.description

        this.authors = new PersonContainer(project)
        this.contributors = new PersonContainer(project)
        this.entrypoints = new EntrypointContainer(project)
    }

    void setVersion(Object version) {
        this.version = version as String
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

    void depends(Map<String, Object> dependencies) {
        this.depends.add(dependencies)
    }

    void recommends(Map<String, Object> dependencies) {
        this.recommends.add(dependencies)
    }

    void suggests(Map<String, Object> dependencies) {
        this.suggests.add(dependencies)
    }

    void breaks(Map<String, Object> dependencies) {
        this.breaks.add(dependencies)
    }

    void conflicts(Map<String, Object> dependencies) {
        this.conflicts.add(dependencies)
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

    void license(String license) {
        this.license.licenses.add(license)
    }

    void licenses(String... licenses) {
        this.license.licenses.addAll(licenses)
    }

    void icon(Map<Integer, String> icons) {
        icons.each {entry ->
            if (icons.containsValue(null)) {
                throw new IllegalArgumentException("null values are not allowed.")
            }

            if (icons.size() > 1 && icons.containsKey(null)) {
                throw new IllegalArgumentException("An icon for all sizes was already specified.")
            }
        }

        this.icon.icons = icons
    }

    void icon(String path) {
        if (this.icon.icons.size() !== 0) {
            throw new IllegalStateException("An icon for a specific size was already specified.")
        }

        this.icon.icons[null] = path
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        var json = new JsonObject()

        serializableFields.each {field ->
            field.trySetAccessible()
            var value = field.get(this)

            if (value !== null && !(value instanceof Container && (value as Container).empty || value instanceof Collection && (value as Collection).empty)) {
                json.add(field.name, context.serialize(value))
            }
        }

        return json
    }

    private Object configure(Object object, Closure configuration) {
        return FabricModGradle.configure(this.project, object, configuration)
    }
}
