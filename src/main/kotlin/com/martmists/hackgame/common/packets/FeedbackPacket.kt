package com.martmists.hackgame.common.packets

import com.martmists.hackgame.common.entities.TextColor
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackPacket(var msg: String) {
    init {
        msg = if (msg.endsWith(TextColor.ANSI.WHITE.toString())) msg else "$msg${TextColor.ANSI.WHITE}"
    }
}
