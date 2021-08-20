package net.auoeke.fabricmodgradle.extension

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet

@CompileStatic
class Extension {
    public final Map<SourceSet, Metadata> metadata = [:]

    private final Project project

    Extension(Project project) {
        this.project = project
    }

    @Override
    Object invokeMethod(String name, Object args) {
        var metadata = this.metadata.computeIfAbsent(this.project.extensions.getByType(JavaPluginExtension).sourceSets.getByName(name), set -> new Metadata(this.project))
        this.project.configure(metadata, (args as Object[])[0] as Closure)

        return metadata
    }
}
