package com.martmists.hackgame.server.game.vfs

import kotlinx.serialization.Serializable

@Serializable
data class VFSFile(val filename: String, var contents: String)