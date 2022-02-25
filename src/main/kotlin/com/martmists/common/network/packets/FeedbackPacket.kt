package com.martmists.common.network.packets

import com.martmists.common.ext.reset
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackPacket(var msg: String) {
    init {
        msg = msg.reset()
    }
}
