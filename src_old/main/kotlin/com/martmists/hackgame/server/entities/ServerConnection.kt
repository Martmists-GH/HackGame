package com.martmists.server.entities

import com.martmists.common.BuiltinPackets
import com.martmists.common.DisconnectException
import com.martmists.common.registries.PacketRegistry
import com.martmists.common.internal.Connection
import com.martmists.common.internal.DataException
import com.martmists.common.packets.DisconnectPacket
import com.martmists.server.Server
import com.martmists.server.game.PlayerSession
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.EOFException
import java.lang.Exception
import java.net.Socket
import java.net.SocketTimeoutException

@ExperimentalSerializationApi
class ServerConnection(override val socket: Socket) : Connection() {
    val session = PlayerSession(this)

    @Volatile
    override var connected = true

    override val reader = socket.getInputStream()
    override val writer = socket.getOutputStream()

    fun run() {
        runWriteThread()

        while (connected && socket.isConnected) {
            try {
                val buf = readPacket()
                if (buf.isNotEmpty()) {
                    PacketRegistry.accept(buf, ServerPacketContext(this))
                }
            } catch (e : DataException) {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket(e.message ?: "Unknown Data Error", false), this)
            } catch (e : Exception) {
                when (e) {
                    is SocketTimeoutException, is EOFException -> {
                        close()
                    }
                    is DisconnectException -> {
                        close()
                    }
                    else -> {
                        e.printStackTrace()
                    }
                }

            }
        }
    }

    override fun close() {
        super.close()
        Server.INSTANCE.connected.remove(this)
        BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("Connection closed by host", false), this)
        socket.close()
    }
}
