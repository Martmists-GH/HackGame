package com.martmists.client.network

import com.martmists.common.utilities.Loggable
import com.martmists.common.network.Connection
import com.martmists.common.network.PacketContext
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.atomic.AtomicBoolean

class ClientConnection(socket: Socket) : Connection(socket), Loggable {
    var reconnect = AtomicBoolean(true)
    override val dispatcher = Dispatchers.Default

    override suspend fun asContext(): PacketContext {
        return ClientPacketContext()
    }

    init {
        info("Connected to server at ${socket.remoteAddress.hostname}:${socket.remoteAddress.port}")
    }
}
