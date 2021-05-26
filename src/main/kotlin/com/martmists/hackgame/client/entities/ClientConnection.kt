package com.martmists.hackgame.client.entities

import com.googlecode.lanterna.gui2.dialogs.MessageDialog
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import com.martmists.hackgame.client.Client
import com.martmists.hackgame.client.ui.Screen
import com.martmists.hackgame.common.entities.Connection
import com.martmists.hackgame.common.entities.DataException
import com.martmists.hackgame.common.entities.PacketRegistry
import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.PingPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import com.martmists.hackgame.loader.Main
import java.net.Socket
import kotlin.system.exitProcess

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

        while (connected && socket.isConnected) {
            try {
                val buf = readPacket()
                if (buf.isNotEmpty()) {
                    PacketRegistry.accept(buf, ClientPacketContext(this))
                } else {
                    val chosen = MessageDialog.showMessageDialog(Screen.gui, "Disconnected", "Server stopped responding. Press OK to reconnect.", MessageDialogButton.Close, MessageDialogButton.OK)
                    if (chosen == MessageDialogButton.Close) {
                        exitProcess(0)
                    } else {
                        Client.INSTANCE.reconnect(socket.inetAddress.hostName, socket.port)
                    }
                }
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