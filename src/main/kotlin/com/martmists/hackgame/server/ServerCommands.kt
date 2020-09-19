package com.martmists.hackgame.server

import com.martmists.hackgame.server.entities.CommandBuilder
import com.martmists.hackgame.server.entities.ServerCommandSource
import com.mojang.brigadier.CommandDispatcher

object ServerCommands {
    fun initialize(dispatcher: CommandDispatcher<ServerCommandSource>) {
        CommandBuilder.builder(dispatcher) {

        }
    }
}
