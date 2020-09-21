package com.martmists.hackgame.server.entities

import com.martmists.hackgame.client.entities.ClientPacketContext
import com.martmists.hackgame.common.entities.Connection
import com.martmists.hackgame.common.entities.DataException
import com.martmists.hackgame.common.entities.PacketRegistry
import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import com.martmists.hackgame.server.Server
import com.martmists.hackgame.server.game.PlayerSession
import org.slf4j.Logger
import java.io.EOFException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import java.net.Socket
import java.net.SocketTimeoutException

class ServerConnection(override val socket: Socket) : Connection() {
    val session = PlayerSession()

    @Volatile
    override var connected = true

    override val reader = socket.getInputStream()
    override val writer = socket.getOutputStream()
    override val logger = Server.INSTANCE.LOGGER

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
                        logger.info("Stopped receiving data from client, disconnecting...")
                        close()
                    }
                    else -> {
                        logger.warn("Unhandled exception: ${e.message ?: e}")
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