package com.martmists.hackgame.server.events

import com.martmists.hackgame.common.entities.ActionResult
import com.martmists.hackgame.common.events.Event
import com.martmists.hackgame.server.game.HostDevice

fun interface PlayerLifecycleEvents {
    fun invoke(username: String, host: HostDevice): ActionResult

    companion object {
        val REGISTER = Event<PlayerLifecycleEvents> {
            PlayerLifecycleEvents { username, host ->
                ActionResult.all(it) { cb ->
                    cb.invoke(username, host)
                }
            }
        }

        val LOGIN = Event<PlayerLifecycleEvents> {
            PlayerLifecycleEvents { username, host ->
                ActionResult.all(it) { cb ->
                    cb.invoke(username, host)
                }
            }
        }
    }
}