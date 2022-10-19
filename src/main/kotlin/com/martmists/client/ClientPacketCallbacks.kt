package com.martmists.client

import com.martmists.client.network.ClientPacketContext
import com.martmists.common.network.BuiltinPackets
import com.martmists.common.utilities.TextColor

object ClientPacketCallbacks {
    fun initialize() {
        BuiltinPackets.FEEDBACK.handler<ClientPacketContext> {
            Client.gui.log.addLine(it.msg)
        }

        BuiltinPackets.BROADCAST.handler<ClientPacketContext> {
            Client.gui.log.addLine("${TextColor.ANSI.BLUE}[BROADCAST] ${TextColor.RESET}${it.msg}")
        }
    }
}
