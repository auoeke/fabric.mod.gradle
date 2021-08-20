package net.auoeke.fabricmodgradle

import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.entrypoint.EntrypointContainer
import net.auoeke.fabricmodgradle.mixin.MixinContainer
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet

@SuppressWarnings("unused")
@CompileStatic
class Extension {
    private static final List<String> environmentValues = [null, "client", "server", "*"]

    public transient final String name

    // mandatory
    public int schemaVersion = 1
    public String id
    public String version

    public String modName
    public String description

    // optional
    public String environment
    public EntrypointContainer entrypoints
    public LanguageAdapterContainer languageAdapters = new LanguageAdapterContainer()
    public MixinContainer mixins = new MixinContainer()
    public JarContainer jars = new JarContainer()

    private transient final Project project

    Extension(Project project, String name) {
        this.project = project
        this.entrypoints = new EntrypointContainer(project)
        this.name = name

        this.id = project.getName()
        this.setVersion(project.getVersion())
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
        this.project.configure(this.entrypoints, configuration)
    }

    void jars(String... paths) {
        this.jars.jars.addAll(paths)
    }

    void jar(String path) {
        this.jars.jars.add(path)
    }

    void languageAdapters(Closure configuration) {
        this.project.configure(this.languageAdapters, configuration)
    }

    void languageAdapter(String key, String type) {
        this.languageAdapters[key] = type
    }

    void mixins(Closure configuration) {
        this.project.configure(this.mixins, configuration)
    }

    void mixins(String... configurations) {
        configurations.each {this.mixin(it)}
    }

    void mixin(String configuration) {
        this.mixin(null, configuration)
    }

    void mixin(String environment, String configuration) {
        this.mixins.add(environment, configuration)
    }
}
