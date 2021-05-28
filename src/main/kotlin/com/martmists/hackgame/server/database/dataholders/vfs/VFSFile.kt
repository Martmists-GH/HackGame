package com.martmists.hackgame.server.database.dataholders.vfs

import kotlinx.serialization.Serializable

@Serializable
data class VFSFile(val filename: String, var contents: String, var isReadOnly: Boolean = false)