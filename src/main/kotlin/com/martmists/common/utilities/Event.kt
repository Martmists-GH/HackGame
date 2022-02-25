package com.martmists.common.utilities

open class Event<T : Any>(private val _invoker: (List<T>) -> T) {
    private val listeners: MutableList<T> = mutableListOf()

    fun invoker(): T {
        return _invoker.invoke(listeners.toList())
    }

    fun addListener(listener: T) {
        listeners.add(listener)
    }
}
