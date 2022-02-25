package com.martmists.server.events

import com.martmists.common.api.ActionResult
import com.martmists.common.api.Event
import com.martmists.server.game.HostDevice
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
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
