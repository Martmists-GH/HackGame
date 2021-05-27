package com.martmists.hackgame.server

import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.FeedbackPacket
import com.martmists.hackgame.common.packets.HostDisconnectPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import com.martmists.hackgame.server.entities.CommandBuilder
import com.martmists.hackgame.server.entities.ServerCommandSource
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType

object ServerCommands {
    fun initialize(dispatcher: CommandDispatcher<ServerCommandSource>) {
        CommandBuilder.builder(dispatcher) {
            command("money") {
                executes {
                    BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${it.source.currentHost.money}$"), it.source.connection)
                }
            }

            command("ls", "dir") {
                executes {
                    val tree = it.source.currentHost.filesystem.generateView()
                    BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket(tree), it.source.connection)
                }

                argument("path", StringArgumentType.string()) {
                    executes {
                        val root = it.source.currentHost.filesystem.getDirByPath(StringArgumentType.getString(it, "path"))
                        val tree = root.generateView()
                        BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket(tree), it.source.connection)
                    }
                }
            }

            command("cat") {
                argument("path", StringArgumentType.string()) {
                    executes {
                        val file = it.source.currentHost.filesystem.getFileByPath(StringArgumentType.getString(it, "path"))
                        BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket(file.contents), it.source.connection)
                    }
                }
            }

            command("rm") {
                argument("path", StringArgumentType.string()) {
                    executes {
                        val parts = StringArgumentType.getString(it, "path").split("/").toMutableList()
                        val file = parts.removeLast()
                        val dir = it.source.currentHost.filesystem.getDirByPath(parts.joinToString("/"))
                        dir.removeFile(file)
                    }
                }
            }

            command("rmdir") {
                argument("path", StringArgumentType.string()) {
                    executes {
                        val parts = StringArgumentType.getString(it, "path").split("/").toMutableList()
                        val dirname = parts.removeLast()
                        val dir = it.source.currentHost.filesystem.getDirByPath(parts.joinToString("/"))
                        dir.removeDir(dirname)
                    }
                }
            }

            command("connect") {
                argument("ip", StringArgumentType.string()) {
                    executes {
                        it.source.connection.session.connectTo(StringArgumentType.getString(it, "ip"))
                    }
                }
            }

            command("disconnect") {
                executes {
                    if (it.source.currentHost == it.source.ownHost) {
                        BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("ERROR: Cannot disconnect from root"), it.source.connection)
                    } else {
                        val last = it.source.session.connectChain.pop()
                        it.source.session.currentIP = it.source.currentHost.ip
                        BuiltinPackets.HOST_DISCONNECT_S2C.send(HostDisconnectPacket(it.source.currentHost.ip, last.ip), it.source.connection)
                    }
                }
            }

            command("exit") {
                executes {
                    BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("exit", false), it.source.connection)
                    it.source.connection.close()
                }
            }

            command("help") {
                // Explain the game
            }

            command("commands") {
                executes {
                    val commands = dispatcher.root.children.joinToString("\n") { c -> "- ${c.name}" }
                    BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("Commands:\n$commands"), it.source.connection)
                }

                argument("cmd", StringArgumentType.word()) {
                    executes {
                        val command = dispatcher.root.getChild(StringArgumentType.getString(it, "cmd")) ?: null
                        // TODO:
                        // Error message if command is null
                        // Command usage if command is not null
                    }
                }
            }
        }
    }
}
