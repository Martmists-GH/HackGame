package com.martmists.client.ui.events

import com.martmists.common.api.Event

fun interface KeyEvent {
    fun invoke(key: Int, scancode: Int, action: Int, mods: Int)

    companion object {
        val EVENT = Event<KeyEvent> { callbacks ->
            KeyEvent { key, scancode, action, mods ->
                for (callback in callbacks) {
                    callback.invoke(key, scancode, action, mods)
                }
            }
        }
    }
}
