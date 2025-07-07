package com.denchic45.binlist.data.api.util

sealed class OptionalProperty<out T> {

    data object NotPresent : OptionalProperty<Nothing>()

    data class Present<T>(val value: T) : OptionalProperty<T>()

    inline fun onPresent(block: (value: T) -> Unit): OptionalProperty<T> {
        if (this is Present) block(value)
        return this
    }

    inline fun onMissing(block: () -> Unit): OptionalProperty<T> {
        if (this is NotPresent) block()
        return this
    }

    val isPresent: Boolean
        get() = this is Present
}

fun <T> optPropertyOf(value: T) = OptionalProperty.Present(value)

fun <T> OptionalProperty<T>.presentOrNull(): T? {
    return (this as? OptionalProperty.Present)?.value
}


fun <T> OptionalProperty<T>.requirePresent(): T {
    return presentOrElse { throw IllegalStateException("Value not present") }
}

fun <T> OptionalProperty<T>.presentOrElse(defaultValue: () -> T) =
    (this as? OptionalProperty.Present)?.value ?: defaultValue()