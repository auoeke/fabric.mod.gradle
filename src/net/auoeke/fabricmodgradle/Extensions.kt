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

inline fun closure(noinline lambda: (Any?) -> Unit): Closure<*> = object : Closure<Any?>(lambda) {
    override fun call(arg: Any?): Any = (owner as (Any?) -> Any?)

    @Suppress("unused")
    fun doCall(arg: Any?): Any = doCall(arg)
}

inline fun <K, V> Map<K, V>.eachValue(action: (V) -> Unit) {
    this.forEach {action(it.value)}
}

inline val <T> T.iterable: Iterable<T> get() = when (this) {
    is Iterable<*> -> this as Iterable<T>
    else -> listOf(this)
}

inline val String?.json get() = JsonPrimitive(this)
