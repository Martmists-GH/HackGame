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
                    if (parts.first() == "bin") {
                        BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}Cannot delete files in bin"))
                        return@action
                    }
                    val file = parts.removeLast()
                    val dir = host.filesystem.getDirByPath(parts.joinToString("/"))
                    dir.removeFile(file)
                }
            }
        }

        command("rmdir") {
            argument("path", StringArgumentType.greedy()) { path ->
                action {
                    val parts = path().split("/").toMutableList()
                    if (parts.first() == "bin") {
                        BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}Cannot delete folders in bin"))
                        return@action
                    }
                    val dirname = parts.removeLast()
                    val dir = host.filesystem.getDirByPath(parts.joinToString("/"))
                    dir.removeDir(dirname)
                }
            }
        }

        command("mkdir") {
            argument("path", StringArgumentType.greedy()) { path ->
                action {
                    val parts = path().split("/").toMutableList()
                    if (parts.first() == "bin") {
                        BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("${TextColor.ANSI.RED}Cannot create folders in bin"))
                        return@action
                    }
                    val dirname = parts.removeLast()
                    val dir = host.filesystem.getDirByPath(parts.joinToString("/"))
                    dir.addDir(dirname)
                }
            }
        }
    }
}
