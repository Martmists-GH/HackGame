package com.martmists.server.events

import com.martmists.common.api.ActionResult
import com.martmists.common.api.Event
import com.martmists.server.Server
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
fun interface ServerLifecycleEvents {
    fun invoke(server: Server): ActionResult

    companion object {
        val START = Event<ServerLifecycleEvents> {
            ServerLifecycleEvents { server ->
                ActionResult.all(it) { event ->
                    event.invoke(server)
                }
            }
        }
    }
}
