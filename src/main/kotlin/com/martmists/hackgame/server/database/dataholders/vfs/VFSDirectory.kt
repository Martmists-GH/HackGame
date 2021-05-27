package com.martmists.hackgame.server.database.dataholders.vfs

import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileNotFoundException

@Serializable
data class VFSDirectory @JvmOverloads constructor(val name: String, var directories: List<VFSDirectory> = emptyList(), var files: List<VFSFile> = emptyList()) {
    fun addDir(name: String) : VFSDirectory {
        if (name == "root") {
            throw IllegalArgumentException("Cannot create dir with name 'root'")
        }

        if (directories.any { it.name == name }) {
            throw FileAlreadyExistsException(File(name))
        }

        return VFSDirectory(name).also {
            directories = listOf(*directories.toTypedArray(), it)
        }
    }

    fun getDir(name: String) : VFSDirectory {
        return directories.firstOrNull { it.name == name } ?: throw FileNotFoundException(name)
    }

    fun getOrCreateDir(name: String) : VFSDirectory {
        return directories.firstOrNull { it.name == name } ?: addDir(name)
    }

    fun addFile(file: String) : VFSFile {
        return VFSFile(file, "").also {
            files = listOf(*files.toTypedArray(), it)
        }
    }

    fun getFile(file: String) : VFSFile {
        return files.firstOrNull { it.filename == file } ?: throw FileNotFoundException(name)
    }

    fun getOrCreateFile(name: String) : VFSFile {
        return files.firstOrNull { it.filename == name } ?: addFile(name)
    }

    fun getDirByPath(path: String) : VFSDirectory {
        var dir = this
        for (sub in path.split("/")) {
            dir = dir.getDir(sub)
        }
        return dir
    }

    fun getFileByPath(path: String) : VFSFile {
        var dir = this
        val parts = path.split("/").toMutableList()
        val file = parts.removeLast()
        for (sub in parts) {
            dir = dir.getDir(sub)
        }
        return dir.getFile(file)
    }

    fun generateView() : String {
        var buf = if (name == "root") "" else "$name\n"
        for (directory in directories) {
            val text = directory.generateView()
            for (line in text.split("\n")) {
                buf += "  $line\n"
            }
        }
        for (file in files) {
            buf += "- ${file.filename}\n"
        }
        return buf
    }

    fun removeFile(file: String) {
        files = files.toMutableList().also { it.removeIf { itt -> itt.filename == file } }.toList()
    }

    fun removeDir(dir: String) {
        directories = directories.toMutableList().also { it.removeIf { itt -> itt.name == dir } }.toList()
    }

    companion object {
        fun empty() = VFSDirectory("root")
    }
}
