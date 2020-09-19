package com.martmists.hackgame.client.entities

import com.googlecode.lanterna.gui2.dialogs.TextInputDialog
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder
import com.martmists.hackgame.client.Client
import com.martmists.hackgame.client.ui.Screen
import com.martmists.hackgame.common.entities.Connection
import com.martmists.hackgame.common.entities.DataException
import com.martmists.hackgame.common.entities.PacketRegistry
import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.PingPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import org.slf4j.Logger
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class ClientConnection(override val socket: Socket) : Connection() {
    // These need to have the opposite order from the server, or we'll get deadlocked
    override val writer = socket.getOutputStream()
    override val reader = socket.getInputStream()
    override val logger = Client.INSTANCE.LOGGER

    @Volatile
    override var connected = true

    fun run() {
        runWriteThread()

        // Initial ping
        BuiltinPackets.PING_C2S.send(PingPacket(0, 0), this)

        // TODO: Input dialog


        while (connected && socket.isConnected) {
            try {
                val buf = readPacket()
                PacketRegistry.accept(buf, ClientPacketContext(this))
            } catch (e: DataException) {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket(e.message ?: "Unknown Data Error", false), this)
            }
        }
    }

    override fun close() {
        super.close()
        socket.close()
    }
}