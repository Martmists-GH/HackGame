package com.martmists.client.ui.events

import com.martmists.client.ui.utils.Size
import com.martmists.common.api.Event

fun interface ResizeEvent {
    fun invoke(size: Size)

    companion object {
        val EVENT = Event<ResizeEvent> { callbacks ->
            ResizeEvent { size ->
                for (callback in callbacks) {
                    callback.invoke(size)
                }
            }
        }
    }
}
