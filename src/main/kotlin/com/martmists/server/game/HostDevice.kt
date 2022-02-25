package com.martmists.server.game

import com.martmists.common.network.BuiltinPackets
import com.martmists.common.network.packets.BroadcastPacket
import com.martmists.server.game.software.Software
import com.martmists.server.game.vfs.VFSDirectory

open class HostDevice(
    val ip: String,
    var connectedUsers: List<ClientSession>,
    var software: List<Software>,
    var money: Int,
    val filesystem: VFSDirectory,
    var password: String
) {
    fun logConnection(sourceIP: String) {
        val dir = filesystem.getOrCreateDir("logs")
        val file = dir.getOrCreateFile("access.log")
        file.contents += "Received log-in from $sourceIP"
    }

    suspend fun broadcast(message: String) {
        connectedUsers.forEach {
            BuiltinPackets.BROADCAST.send(it.connection, BroadcastPacket(message))
        }
    }
}
