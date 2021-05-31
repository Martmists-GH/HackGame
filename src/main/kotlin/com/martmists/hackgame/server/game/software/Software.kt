package com.martmists.hackgame.server.game.software

import com.martmists.hackgame.server.entities.ServerCommandSource
import com.mojang.brigadier.context.CommandContext

interface Software {
    val name: String
    val tier: Int

    val type: Type

    fun invoke(context: CommandContext<ServerCommandSource>)

    enum class Type {
        LOCK,
        EXECUTABLE,
        MALWARE,
        TROPHY
    }
}
