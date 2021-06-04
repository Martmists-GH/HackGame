package com.martmists.hackgame.server.game.software

import com.martmists.hackgame.server.entities.ServerCommandSource
import com.mojang.brigadier.context.CommandContext

class Probe(override val filename: String,
            override val tier: Int) : Software {
    override val name = "probe"
    override val type = Software.Type.EXECUTABLE

    override fun invoke(context: CommandContext<ServerCommandSource>) {
        TODO("Not yet implemented")
    }

    companion object {
        val TIER_1 = Probe("dev_probe", 1)
    }
}
