package com.martmists.hackgame.server.database.dataholders

import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory
import kotlinx.serialization.Serializable

@Serializable
data class StoredHostDevice @JvmOverloads constructor(
        val money: Int,
        val software: List<String> = emptyList(),
        val files: VFSDirectory,
        val password: String,
)
