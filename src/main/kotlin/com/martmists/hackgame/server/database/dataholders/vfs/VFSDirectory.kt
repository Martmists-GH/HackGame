package com.martmists.hackgame.server.database.dataholders.vfs

import kotlinx.serialization.Serializable

@Serializable
data class VFSDirectory(val directories: MutableList<VFSDirectory>, val files: MutableList<VFSFile>) {
    companion object {
        fun empty() = VFSDirectory(mutableListOf(), mutableListOf())
    }
}
