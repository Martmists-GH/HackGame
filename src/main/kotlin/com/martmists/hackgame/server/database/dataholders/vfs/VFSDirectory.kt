package com.martmists.hackgame.server.database.dataholders.vfs

import kotlinx.serialization.Serializable

@Serializable
data class VFSDirectory @JvmOverloads constructor(val name: String, var directories: List<VFSDirectory> = emptyList(), var files: List<VFSFile> = emptyList()) {
    fun addDir(name: String) {
        directories = listOf(*directories.toTypedArray(), VFSDirectory(name))
    }

    fun addFile(file: VFSFile) {
        files = listOf(*files.toTypedArray(), file)
    }

    companion object {
        fun empty() = VFSDirectory("root")
    }
}
