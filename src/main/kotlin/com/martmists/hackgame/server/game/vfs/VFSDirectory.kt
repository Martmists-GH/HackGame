package com.martmists.hackgame.server.game.vfs

import kotlinx.serialization.Serializable

@Serializable
data class VFSDirectory(var directories: List<VFSDirectory>, var files: List<VFSFile>)
