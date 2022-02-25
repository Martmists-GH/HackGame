package com.martmists.server.database.dataholders

import com.martmists.server.game.vfs.VFSDirectory
import kotlinx.serialization.Serializable

@Serializable
data class StoredHostDevice @JvmOverloads constructor(
    val money: Int,
    val software: List<String> = emptyList(),
    val files: VFSDirectory,
    val password: String,
)
