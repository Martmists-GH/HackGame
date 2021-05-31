package com.martmists.hackgame.server.events

import com.martmists.hackgame.common.entities.ActionResult
import com.martmists.hackgame.common.events.Event
import com.martmists.hackgame.server.game.HostDevice

fun interface NpcEvents {
    fun invoke(host: HostDevice): ActionResult

    companion object {
        val SPAWN = Event<NpcEvents> {
            NpcEvents { host ->
                ActionResult.all(it) {
                    it.invoke(host)
                }
            }
        }

        val DESPAWN = Event<NpcEvents> {
            NpcEvents { host ->
                ActionResult.all(it) {
                    it.invoke(host)
                }
            }
        }
    }
}