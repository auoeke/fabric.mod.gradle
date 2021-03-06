package net.auoeke.fabricmodgradle.extension

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import groovy.lang.Closure
import net.auoeke.extensions.asMutable
import net.auoeke.extensions.endsWith
import net.auoeke.fabricmodgradle.*
import net.auoeke.fabricmodgradle.extension.contact.Contact
import net.auoeke.fabricmodgradle.extension.contact.Person
import net.auoeke.fabricmodgradle.extension.contact.PersonContainer
import net.auoeke.fabricmodgradle.extension.entrypoint.EntrypointContainer
import net.auoeke.fabricmodgradle.extension.entrypoint.EntrypointTarget
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import net.auoeke.fabricmodgradle.extension.misc.*
import net.auoeke.fabricmodgradle.extension.mixin.MixinContainer
import net.auoeke.fabricmodgradle.extension.relation.RelationContainer
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.util.Configurable
import org.gradle.util.internal.ConfigureUtil
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "unused")
class Metadata(@Transient val project: Project, @Transient val set: SourceSet, @Transient private val outputDirectory: File) : JsonSerializable, Configurable<Any> {
    @Transient
    var initialized: Boolean? = null

    var schemaVersion: Int = 1
    lateinit var id: String
    var version: String? = null
        set(version) {
            field = version.string
        }
    lateinit var name: String

    var description: String? = null
    private val contact: Contact = Contact()
    private val authors: PersonContainer = PersonContainer(this)
    private val contributors: PersonContainer = PersonContainer(this)
    private val license: LicenseContainer = LicenseContainer()
    private val icon: IconContainer = IconContainer()
    private var environment: String? = null
        set(environment) {
            if (!environmentValues.contains(environment)) {
                throw IllegalArgumentException("""environment must be one of $environmentValues but "$environment" was supplied.""")
            }

            field = environment
        }
    private val entrypoints: EntrypointContainer = EntrypointContainer(this)
    private val languageAdapters: LanguageAdapterContainer = LanguageAdapterContainer()
    private var custom: CustomObject = CustomObject(this)
    private val mixins: MixinContainer = MixinContainer()
    private val depends: RelationContainer = RelationContainer()
    private val recommends: RelationContainer = RelationContainer()
    private val suggests: RelationContainer = RelationContainer()
    private val breaks: RelationContainer = RelationContainer()
    private val conflicts: RelationContainer = RelationContainer()
    private val jars: JarContainer = JarContainer()

    private val classPath: List<Path> by lazy {set.runtimeClasspath.map {it.toPath()}}
    private val types: MutableMap<String, ClassInfo> by lazy {
        HashMap<String, ClassInfo>().also {types ->
            set.output.classesDirs.forEach {directory ->
                directory.walkTopDown().filter {it.isFile && it.extension == "class"}.associateTo(types) {file -> ClassInfo(file.inputStream()).let {it.name to it}}
            }

            types.keys.toTypedArray().forEach {
                processInterfaces(types, it)
            }
        }
    }

    @Transient
    private val classPathTypes: MutableMap<String, ClassInfo?> = HashMap()

    fun entrypoints(configuration: Closure<*>) {
        this.configure(entrypoints, configuration)
    }

    fun entrypoints(entrypoints: Map<*, *>) {
        if (this.entrypoints.entrypoints !== null) {
            throw IllegalStateException("Entrypoints have already been defined.")
        }

        this.entrypoints.entrypoints = entrypoints.entries.associate {entry ->
            entry.key.string to entry.value.iterable.let {value ->
                value.flatMap {
                    when (it) {
                        is Map<*, *> -> when ("value") {
                            in it -> listOf(EntrypointTarget(it))
                            else -> it.map {entry -> EntrypointTarget(entry.value.iterable.map(Any?::string), entry.key.string)}
                        }
                        else -> listOf(EntrypointTarget(it.string))
                    }
                } as MutableList
            }
        } as MutableMap
    }

    fun jars(vararg paths: String) {
        jars.jars += paths
    }

    fun jar(path: String) {
        jars.jars += path
    }

    fun languageAdapters(configuration: Closure<*>) {
        this.configure(languageAdapters, configuration)
    }

    fun languageAdapter(key: String, type: String) {
        languageAdapters.setProperty(key, type)
    }

    fun mixin(configuration: Closure<*>) {
        this.configure(mixins, configuration)
    }

    fun mixin(configuration: String) {
        this.mixin(null, configuration)
    }

    fun mixin(vararg configurations: String) {
        configurations.forEach(this::mixin)
    }

    fun mixin(environment: String? = null, configuration: String) {
        mixins.add(environment, configuration)
    }

    fun depends(dependencies: Map<String, *>) {
        depends.add(dependencies)
    }

    fun recommends(dependencies: Map<String, *>) {
        recommends.add(dependencies)
    }

    fun suggests(dependencies: Map<String, *>) {
        suggests.add(dependencies)
    }

    fun breaks(dependencies: Map<String, *>) {
        breaks.add(dependencies)
    }

    fun conflicts(dependencies: Map<String, *>) {
        conflicts.add(dependencies)
    }

    fun contact(configuration: Closure<*>) {
        this.configure(contact, configuration)
    }

    fun authors(configuration: Closure<*>) {
        this.configure(authors, configuration)
    }

    fun author(author: String, configuration: Closure<*>) {
        authors.add(author, configuration)
    }

    fun author(author: String) {
        authors.people.add(Person(author))
    }

    fun authors(vararg authors: String) {
        authors.forEach(this::author)
    }

    fun contributors(configuration: Closure<*>) {
        this.configure(contributors, configuration)
    }

    fun contributor(author: String, configuration: Closure<*>) {
        contributors.add(author, configuration)
    }

    fun contributor(contributor: String) {
        contributors.people.add(Person(contributor))
    }

    fun contributors(vararg contributors: String) {
        contributors.forEach(this::contributor)
    }

    fun license(license: String) {
        this.license.licenses += license
    }

    fun license(vararg licenses: String) {
        license.licenses += licenses
    }

    fun icon(icons: MutableMap<Int?, String>) {
        require(!icons.containsValue(null as String?)) {"null values are not allowed."}
        require(icons.size <= 1 || null in icons) {"An icon for all sizes was already specified."}

        icon.icons = icons
    }

    fun icon(path: String) {
        if (!icon.icons.empty) {
            throw IllegalStateException("An icon for a specific size was already specified.")
        }

        icon.icons[null] = path
    }

    fun custom(configurator: Closure<*>) {
        custom.configure(configurator)
    }

    fun custom(values: MutableMap<String, Any?>) {
        custom.configure(values)
    }

    override fun toJson(context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        if (entrypoints.empty) {
            types.eachValue {type ->
                type.interfaces.forEach {
                    when (it) {
                        "net/fabricmc/api/ModInitializer" -> entrypoints.add("main", type.binaryName)
                        "net/fabricmc/api/ClientModInitializer" -> entrypoints.add("client", type.binaryName)
                        "net/fabricmc/api/DedicatedServerModInitializer" -> entrypoints.add("server", type.binaryName)
                        "net/fabricmc/loader/api/entrypoint/PreLaunchEntrypoint" -> entrypoints.add("preLaunch", type.binaryName)
                    }
                }
            }
        }

        serializableFields.filter {!it.name.contains('$')}.forEach {field ->
            field.trySetAccessible()
            val value = field.get(this)

            if (value !== null) if ((value !is Container || !value.empty) && (value !is Collection<*> || !value.empty) && (value !is Map<*, *> || !value.empty)) {
                json.add(field.name, context.serialize(value))
            }
        }

        return json
    }

    override fun configure(configurator: Closure<*>?): Any {
        if (initialized != true) {
            initialized = true

            schemaVersion = 1
            id = project.name
            version = project.version.string
            description = project.description

            set.resources.srcDir(outputDirectory)
        }

        return ConfigureUtil.configureSelf(configurator, this) ?: this
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun configure(obj: Any?, configurator: Closure<*>?): Any? {
        var result: Any? = null
        project.configure(obj, configurator?.andThen(closure {result = it}))

        return result
    }

    private fun info(types: MutableMap<String, ClassInfo>, type: String): ClassInfo? = types[type] ?: classPathTypes.computeIfAbsent(type) {
        val iterator = classPath.listIterator().asMutable()

        iterator.forEach {
            if (it.exists()) {
                val filename = it.fileName

                when {
                    filename !== null && filename.toString().endsWith(".jar", ".zip") -> FileSystems.newFileSystem(it).getPath("").also {root -> iterator.set(root)}
                    else -> it
                }.resolve("$type.class").also {file ->
                    if (file.exists()) {
                        return@computeIfAbsent ClassInfo(file.inputStream())
                    }
                }
            } else {
                iterator.remove()
            }
        }

        return@computeIfAbsent null
    }

    private fun processInterfaces(types: MutableMap<String, ClassInfo>, type: String?): Set<String> {
        if (type !== null) {
            info(types, type)?.also {
                val interfaces = it.interfaces.clone() as HashSet<String>
                interfaces += processInterfaces(types, it.superclass)
                interfaces.forEach {iface -> interfaces += processInterfaces(types, iface)}
                it.interfaces = interfaces

                return interfaces
            }
        }

        return emptySet()
    }

    private companion object {
        private val environmentValues: List<String?> = listOf("client", "server", "*")
        private val serializableFields: List<Field> = Metadata::class.java.declaredFields.filter {field -> field.modifiers and (Modifier.STATIC or Modifier.TRANSIENT) == 0}
    }
}
