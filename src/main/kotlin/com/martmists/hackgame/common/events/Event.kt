package com.martmists.hackgame.common.events

open class Event<T>(private val _invoker: (List<T>) -> T) {
    private val listeners: MutableList<T> = mutableListOf()

    fun invoker(): T {
        return _invoker.invoke(listeners.toList())
    }

    fun addListener(listener: T) {
        listeners.add(listener)
    }
}
