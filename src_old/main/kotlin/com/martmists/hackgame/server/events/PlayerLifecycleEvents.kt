package com.martmists.server.events

import com.martmists.common.api.ActionResult
import com.martmists.common.api.Event
import com.martmists.server.game.HostDevice
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
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
