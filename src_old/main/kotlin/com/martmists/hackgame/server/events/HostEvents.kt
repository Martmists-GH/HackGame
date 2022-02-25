package com.martmists.server.events

import com.martmists.common.api.ActionResult
import com.martmists.common.api.Event
import com.martmists.server.game.HostDevice
import com.martmists.server.game.PlayerSession
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
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
