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
        }
    }
}
