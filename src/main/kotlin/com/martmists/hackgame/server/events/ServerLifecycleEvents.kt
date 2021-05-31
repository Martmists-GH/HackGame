package com.martmists.hackgame.server.events

import com.martmists.hackgame.common.entities.ActionResult
import com.martmists.hackgame.common.events.Event
import com.martmists.hackgame.server.Server

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
