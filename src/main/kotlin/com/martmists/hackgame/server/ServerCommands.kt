package com.martmists.hackgame.server

import com.martmists.hackgame.server.entities.CommandBuilder
import com.martmists.hackgame.server.entities.ServerCommandSource
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType

object ServerCommands {
    fun initialize(dispatcher: CommandDispatcher<ServerCommandSource>) {
        CommandBuilder.builder(dispatcher) {
            command("connect") {
                argument("ip", StringArgumentType.string()) {
                    executes {
                        it.source.connection.session.connectTo(StringArgumentType.getString(it, "ip"))

                        1
                    }
                }
            }
        }
    }
}
