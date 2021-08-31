@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package net.auoeke.fabricmodgradle

import com.google.gson.JsonPrimitive
import groovy.lang.Closure
import java.util.*

inline val Any?.string: String get() = Objects.toString(this)
inline val Collection<*>.empty: Boolean get() = this.isEmpty()
inline val Map<*, *>.empty: Boolean get() = this.isEmpty()
inline val Map<*, *>?.emptyOrNull: Boolean get() = this?.empty ?: true

inline fun <reified T> catch(action: () -> Unit) {
    try {
        action()
    } catch (throwable: Throwable) {
        if (!T::class.isInstance(throwable)) {
            throw throwable
        }
    }
}

inline fun <T : (P) -> V, P, V> closure(lambda: T): Closure<V> = object : Closure<V>(lambda) {
    override fun call(arg: Any?): V {
        return (this.owner as T)(arg as P)
    }

    @Suppress("unused")
    fun doCall(arg: Any?): V {
        return this.doCall(arg)
    }
}

inline fun <reified T> Any?.cast(): T {
    if (!T::class.isInstance(this)) {
        throw IllegalArgumentException("${if (this === null) "null" else "$this of ${this.javaClass}"} was passed where a ${T::class.qualifiedName} instance was expected.")
    }

    return this as T
}

inline fun <K, V> Map<K, V>.eachValue(action: (V) -> Unit) {
    this.forEach {action(it.value)}
}

inline fun String.endsWithAny(vararg suffixes: String): Boolean {
    suffixes.forEach {
        if (this.endsWith(it, false)) {
            return true
        }
    }

    return false
}

inline val <T> T.iterable: Iterable<T> get() = when (this) {
    is Iterable<*> -> this as Iterable<T>
    else -> listOf(this)
}

inline val String?.json get() = JsonPrimitive(this)
