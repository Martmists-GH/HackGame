package com.martmists.common.network.packets

import com.martmists.common.ext.reset
import kotlinx.serialization.Serializable

@Serializable
data class DisconnectPacket(
    var reason: String,
    val reconnect: Boolean
) {
    init {
        reason = reason.reset()
    }
}
