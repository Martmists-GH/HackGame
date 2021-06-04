package com.martmists.hackgame.server.game

import com.martmists.hackgame.server.game.software.Software
import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory

open class HostDevice(
        val ip: String,
        var connectedUsers: List<PlayerSession>,
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
}
