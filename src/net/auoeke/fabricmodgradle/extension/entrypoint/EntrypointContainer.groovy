package net.auoeke.fabricmodgradle.extension.entrypoint

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.extension.json.Container
import net.auoeke.fabricmodgradle.extension.json.JsonSerializable
import org.gradle.api.Project

import java.lang.reflect.Method
import java.util.stream.Stream

@CompileStatic
class EntrypointContainer implements JsonSerializable, Container {
    private static final List<Method> methods = (List<Method>) Stream.of(EntrypointContainer.class.declaredMethods).filter(method -> method.name == "add").toList()

    private final Project project
    private final Map<String, List<EntrypointTarget>> entrypoints = [:]

    EntrypointContainer(Project project) {
        this.project = project
    }

    void add(String entrypoint, EntrypointTarget target) {
        this.entrypoints.computeIfAbsent(entrypoint, entrypoint2 -> []).add(target)
    }

    void add(String entrypoint, Closure configuration) {
        var target = new EntrypointTarget()
        this.project.configure(target, configuration)
        this.add(entrypoint, target)
    }

    void add(String entrypoint, String adapter, String type) {
        this.add(entrypoint, new EntrypointTarget(value: type, adapter: adapter))
    }

    void add(String entrypoint, String type) {
        this.add(entrypoint, null, type)
    }

    @Override
    Object invokeMethod(String name, Object args) {
        var argList = (args as Object[]).toList()
        argList.add(0, name)

        for (method in methods) {
            try {
                return method.invoke(this, argList.toArray())
            } catch (IllegalArgumentException ignored) {}
        }

        return null
    }

    @Override
    JsonElement toJson(JsonSerializationContext context) {
        return context.serialize(this.entrypoints)
    }

    @Override
    boolean isEmpty() {
        return this.entrypoints.isEmpty()
    }
}
