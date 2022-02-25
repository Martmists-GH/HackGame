package com.martmists.server.network

import com.martmists.common.network.PacketContext
import com.martmists.server.game.ClientSession

class ServerPacketContext(val session: ClientSession) : PacketContext {
    val connection = session.connection
}
