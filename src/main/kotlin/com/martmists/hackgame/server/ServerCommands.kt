package com.martmists.hackgame.server

import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.FeedbackPacket
import com.martmists.hackgame.common.packets.HostDisconnectPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import com.martmists.hackgame.server.entities.CommandBuilder
import com.martmists.hackgame.server.entities.ServerCommandSource
import com.martmists.hackgame.common.entities.TextColor
import com.martmists.hackgame.server.game.HostManager
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import kotlinx.serialization.ExperimentalSerializationApi

object ServerCommands {
    fun initialize(dispatcher: CommandDispatcher<ServerCommandSource>) {
        CommandBuilder.builder(dispatcher) {
            command("money") {
                executes {
                    BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.YELLOW}${it.source.currentHost.money}$"), it.source.connection)
                }
            }

            command("ls") {
                executes {
                    val tree = it.source.currentHost.filesystem.generateView()
                    BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket(tree), it.source.connection)
                }

                argument("path", StringArgumentType.greedyString()) {
                    executes {
                        val root = it.source.currentHost.filesystem.getDirByPath(StringArgumentType.getString(it, "path"))
                        val tree = root.generateView()
                        BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket(tree), it.source.connection)
                    }
                }
            }

            command("cat") {
                argument("path", StringArgumentType.greedyString()) {
                    executes {
                        val file = it.source.currentHost.filesystem.getFileByPath(StringArgumentType.getString(it, "path"))
                        BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket(file.contents), it.source.connection)
                    }
                }
            }

            command("rm") {
                argument("path", StringArgumentType.greedyString()) {
                    executes {
                        val parts = StringArgumentType.getString(it, "path").split("/").toMutableList()
                        if (parts.first() == "bin") {
                            BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.RED}Cannot delete files in bin"), it.source.connection)
                            return@executes
                        }
                        val file = parts.removeLast()
                        val dir = it.source.currentHost.filesystem.getDirByPath(parts.joinToString("/"))
                        dir.removeFile(file)
                    }
                }
            }

            command("rmdir") {
                argument("path", StringArgumentType.greedyString()) {
                    executes {
                        val parts = StringArgumentType.getString(it, "path").split("/").toMutableList()
                        if (parts.first() == "bin") {
                            BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.RED}Cannot delete folders in bin"), it.source.connection)
                            return@executes
                        }
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
                        BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.RED}ERROR: Cannot disconnect from root"), it.source.connection)
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
                executes {
                    BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("TODO: Put guide here"), it.source.connection)
                }
            }

            command("commands") {
                executes {
                    val commands = dispatcher.root.children.filter { itt -> itt.requirement?.test(it.source) ?: true }.joinToString("\n") { c -> "- ${TextColor.ANSI.GREEN}${c.name}${TextColor.ANSI.WHITE}" }
                    BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("Commands:\n$commands"), it.source.connection)
                }

//                argument("cmd", StringArgumentType.word()) {
//                    executes {
//                        val command = dispatcher.root.getChild(StringArgumentType.getString(it, "cmd")) ?: null
//                        // TODO:
//                        // Error message if command is null
//                        // Command usage if command is not null
//                    }
//                }
            }

            command("resetpass") {
                executes {
                    it.source.currentHost.apply {
                        password = HostManager.generateRandomPass()
                        HostManager.updateToDB(ip)
                    }
                }
            }

            command("software") {
                executes {
                    BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.WHITE}Usage: software [install|uninstall|list]"), it.source.connection)
                }

//                literal("install") {
//                    // Move to bin folder
//                    argument("software", StringArgumentType.string()) {
//                        executes {
//                            // If file exists, install, add to software and move
//                            // If file not exists, error
//                        }
//                    }
//                }
//
//                literal("uninstall") {
//                    // move to software folder
//                }
//
//                literal("list") {
//                    // list installed software
//                }
            }
        }
    }
}
