package com.martmists.server.game.software

import com.martmists.commandparser.arguments.StringArgumentType
import com.martmists.commandparser.dsl.BuildCommandContext
import com.martmists.server.commands.IpArgumentType
import com.martmists.server.commands.ServerCommandContext
import com.martmists.server.game.HostManager
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class Probe(override val filename: String,
            override val tier: Int) : Software() {
    override val name = "probe"
    override val type = Type.EXECUTABLE

    override fun BuildCommandContext<ServerCommandContext>.buildCommand() {
        argument("target", IpArgumentType.ip()) { ip ->
            argument("password", StringArgumentType.word()) { pass ->
                action {
                    val remote = HostManager.getHost(ip())
                    if (remote.password == pass()) {
                        // TODO: Connect
                    } else {
                        session.connectTo(remote)
                    }
                }
            }
        }
    }

    companion object {
        val TIER_1 = Probe("dev_probe", 1)
    }
}
