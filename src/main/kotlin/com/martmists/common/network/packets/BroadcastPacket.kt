package com.martmists.common.network.packets

import com.martmists.common.utilities.TextColor
import kotlinx.serialization.Serializable

@Serializable
data class BroadcastPacket(var msg: String) {
    init {
        msg = if (msg.endsWith(TextColor.ANSI.WHITE.toString())) msg else "$msg${TextColor.ANSI.WHITE}"
    }
}
