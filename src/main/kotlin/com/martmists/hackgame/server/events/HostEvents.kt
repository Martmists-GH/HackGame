package com.martmists.hackgame.server.events

import com.martmists.hackgame.common.entities.ActionResult
import com.martmists.hackgame.common.events.Event
import com.martmists.hackgame.server.game.HostDevice
import com.martmists.hackgame.server.game.PlayerSession

fun interface HostEvents {
    fun invoke(host: HostDevice, source: HostDevice, player: PlayerSession): ActionResult

    companion object {
        val BEFORE_CONNECT = Event<HostEvents> { callbacks ->
            HostEvents { host, source, player ->
                ActionResult.conditionally(callbacks) {
                    it.invoke(host, source, player)
                }
            }
        }

        val AFTER_CONNECT = Event<HostEvents> { callbacks ->
            HostEvents { host, source, player ->
                ActionResult.all(callbacks) {
                    it.invoke(host, source, player)
                }
            }
        }
    }
}
