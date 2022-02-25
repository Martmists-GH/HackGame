package com.martmists.server.game

import com.martmists.common.network.BuiltinPackets
import com.martmists.common.network.packets.FeedbackPacket
import com.martmists.common.network.packets.HostConnectPacket
import com.martmists.common.network.packets.HostDisconnectPacket
import com.martmists.common.utilities.TextColor
import com.martmists.server.network.ServerConnection
import java.util.*

class ClientSession(val connection: ServerConnection) {
    var isLoggedIn = false

    private val connectChain = Stack<HostDevice>()
    val device: HostDevice?
        get() = if (connectChain.empty()) null else connectChain.peek()

    suspend fun connectTo(remote: HostDevice) {
        if (!connectChain.isEmpty()) {
            if (connectChain.any { it == remote }) {
                BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}You are already connected to this device"))
                return
            }

            remote.logConnection(this.device!!.ip)
        }

        connectChain.push(remote)
        BuiltinPackets.HOST_CONNECT.send(connection, HostConnectPacket(remote.ip))
    }

    suspend fun disconnect() {
        if (connectChain.size == 1) {
            BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}You cannot disconnect from your root system"))
        }

        val last = connectChain.pop()
        BuiltinPackets.HOST_DISCONNECT.send(connection, HostDisconnectPacket(device!!.ip, last.ip))
    }
}
