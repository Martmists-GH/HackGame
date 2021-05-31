package com.martmists.hackgame.server.events

import com.martmists.hackgame.common.entities.ActionResult
import com.martmists.hackgame.common.events.Event
import com.martmists.hackgame.server.game.HostDevice
import com.martmists.hackgame.server.game.PlayerSession

fun interface ConnectEvent {
    fun invoke(host: HostDevice, source: HostDevice, player: PlayerSession): ActionResult

    companion object {
        val BEFORE = Event<ConnectEvent> { callbacks ->
            ConnectEvent { host, source, player ->
                ActionResult.conditionally(callbacks) {
                    it.invoke(host, source, player)
                }
            }
        }

        val AFTER = Event<ConnectEvent> { callbacks ->
            ConnectEvent { host, source, player ->
                ActionResult.all(callbacks) {
                    it.invoke(host, source, player)
                }
            }
        }
    }
}
