package com.martmists.client.commands

import com.martmists.client.Client
import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dsl.build
import com.martmists.common.network.BuiltinPackets
import com.martmists.common.network.packets.LoginPacket

object ClientCommands {
    fun register(dispatcher: Dispatcher<ClientCommandContext>) {
        build(dispatcher) {
            command("dev_login") {
                action {
                    BuiltinPackets.LOGIN.send(connection, LoginPacket("dev", "dev", false))
                }
            }

            command("clear") {
                action {
                    Client.gui.log.clear()
                }
            }
        }
    }
}
