package com.martmists.hackgame.server.database.dataholders

import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory
import kotlinx.serialization.Serializable

@Serializable
data class StoredHostDevice(
        val money: Int,
        val software: List<String>,
        val files: VFSDirectory
)
