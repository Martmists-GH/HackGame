package com.martmists.hackgame.server

import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.FeedbackPacket
import com.martmists.hackgame.common.packets.PingPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import com.martmists.hackgame.common.entities.TextColor
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
            if (context.session.isLoggedIn) {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("Duplicate login packet received!", true), context.connection)
            }

            context.session.onLoginPacket(packet)
        }

        BuiltinPackets.COMMAND_C2S.handler { packet, context ->
            if (!context.session.isLoggedIn) {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("No login packet received!", true), context.connection)
            }

            try {
                context.session.onCommandPacket(packet)
            } catch(e: Exception) {
                val error = e.message ?: "Unknown Error"
                BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.RED}ERROR: $error"), context.connection)
            }
        }
    }

    fun initialize() {
        Server.INSTANCE.LOGGER.info("Registered server packet callbacks")
    }
}