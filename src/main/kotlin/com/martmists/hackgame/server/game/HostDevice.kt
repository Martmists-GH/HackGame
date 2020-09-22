package com.martmists.hackgame.server.game

import com.martmists.hackgame.server.game.software.Software
import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory

data class HostDevice(
        val ip: String,
        var software: List<Software>,
        var money: Int,
        val filesystem: VFSDirectory
)
