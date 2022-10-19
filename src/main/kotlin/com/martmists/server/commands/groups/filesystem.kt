package com.martmists.server.commands.groups

import com.martmists.commandparser.arguments.StringArgumentType
import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dsl.build
import com.martmists.common.network.BuiltinPackets
import com.martmists.common.network.packets.FeedbackPacket
import com.martmists.common.utilities.TextColor
import com.martmists.server.commands.ServerCommandContext


fun registerFs(dispatcher: Dispatcher<ServerCommandContext>) {
    build(dispatcher) {
        command("ls") {
            argument("path", StringArgumentType.string(), default = "/") { path ->
                action {
                    val fs = session.device!!.filesystem
                    val root = fs.getDirByPath(path())
                    BuiltinPackets.FEEDBACK.send(
                        connection,
                        FeedbackPacket(
                            root.generateView()
                        )
                    )
                }
            }
        }

        command("cat") {
            argument("path", StringArgumentType.greedy()) { path ->
                action {
                    val file = host.filesystem.getFileByPath(path())
                    BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket(file.contents))
                }
            }
        }

        command("rm") {
            argument("path", StringArgumentType.greedy()) { path ->
                action {
                    val parts = path().split("/").toMutableList()
                    val file = parts.removeLast()
                    val dir = host.filesystem.getDirByPath(parts.joinToString("/"))
                    if (dir.isReadOnly) {
                        BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}Cannot delete files in read-only directory"))
                        return@action
                    }
                    dir.removeFile(file)
                }
            }
        }

        command("rmdir") {
            argument("path", StringArgumentType.greedy()) { path ->
                action {
                    val parts = path().split("/").toMutableList()
                    val dirname = parts.removeLast()
                    val dir = host.filesystem.getDirByPath(parts.joinToString("/"))
                    if (dir.isReadOnly) {
                        BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}Cannot delete files in read-only directory"))
                        return@action
                    }
                    dir.removeDir(dirname)
                }
            }
        }

        command("mkdir") {
            argument("path", StringArgumentType.greedy()) { path ->
                action {
                    val parts = path().split("/").toMutableList()
                    val dirname = parts.removeLast()
                    val dir = host.filesystem.getDirByPath(parts.joinToString("/"))
                    if (dir.isReadOnly) {
                        BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}Cannot create files in read-only directory"))
                        return@action
                    }
                    dir.addDir(dirname)
                }
            }
        }

        command("mv") {
            argument("from", StringArgumentType.string()) { from ->
                argument("to", StringArgumentType.string()) { to ->
                    action {
                        val parts = from().split("/").toMutableList()
                        val target = parts.removeLast()
                        val fromDir = host.filesystem.getDirByPath(parts.joinToString("/"))
                        if (fromDir.isReadOnly) {
                            BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}Cannot move files in read-only directory"))
                            return@action
                        }
                        val toDir = host.filesystem.getDirByPath(to())
                        if (toDir.isReadOnly) {
                            BuiltinPackets.FEEDBACK.send(
                                connection,
                                FeedbackPacket("${TextColor.ANSI.RED}Cannot move files into write-protected directory")
                            )
                            return@action
                        }

                        if (fromDir.directories.any { it.name == target }) {
                            // move folder
                            val source = fromDir.getDir(target)
                            toDir.addDir(target).also {
                                it.files = source.files
                                it.directories = source.directories
                            }
                        } else {
                            // move file
                            val file = fromDir.getFile(target)
                            toDir.addFile(target).also {
                                it.contents = file.contents
                            }
                            fromDir.removeFile(target)
                        }

                        BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.GREEN}Moved file(s)!"))
                    }
                }
            }
        }
    }
}
