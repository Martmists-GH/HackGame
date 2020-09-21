package com.martmists.hackgame.server

import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.PingPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import com.martmists.hackgame.server.entities.ServerCommandSource
import java.lang.Exception
import kotlin.random.Random

object ServerPacketCallbacks {
    init {
        BuiltinPackets.DISCONNECT_C2S.handler { packet, context ->
            Server.INSTANCE.LOGGER.info("Client disconnected with reason: ${packet.reason}")
            context.connection.close()
        }

        BuiltinPackets.PING_C2S.handler { packet, context ->
            // TODO: Check last ping value
            BuiltinPackets.PING_S2C.send(PingPacket(packet.current, Random.nextInt()), context.connection)
        }

        BuiltinPackets.LOGIN_C2S.handler { packet, context ->
            if (context.connection.session.isLoggedIn) {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("Duplicate login packet received!", true), context.connection)
            }

            context.connection.session.onLoginPacket(packet)
        }

        BuiltinPackets.COMMAND_C2S.handler { packet, context ->
            if (!context.connection.session.isLoggedIn) {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("No login packet received!", true), context.connection)
            }

            try {
                val result = Server.INSTANCE.dispatcher.execute(packet.cmd, ServerCommandSource(context.connection))
            } catch(e: Exception) {
                val error = e.message ?: "Unknown Error"
            }
            // TODO: Send to client
        }
    }

    fun initialize() {
        Server.INSTANCE.LOGGER.info("Registered server packet callbacks")
    }
}