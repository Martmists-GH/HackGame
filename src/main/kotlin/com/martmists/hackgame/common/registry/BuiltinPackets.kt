package com.martmists.hackgame.common.registry

import com.martmists.hackgame.common.entities.PacketRegistry
import com.martmists.hackgame.common.packets.*

object BuiltinPackets {
    // === Server to Client ===
    val DISCONNECT_S2C = PacketRegistry.registerS2C("hackgame:disconnect", DisconnectPacket::class.java)
    val PING_S2C = PacketRegistry.registerS2C("hackgame:ping", PingPacket::class.java)
    val FEEDBACK_S2C = PacketRegistry.registerS2C("hackgame:message", FeedbackPacket::class.java)

    val HOST_CONNECT_S2C = PacketRegistry.registerS2C("hackgame:host_connect", HostConnectPacket::class.java)
    val HOST_DISCONNECT_S2C = PacketRegistry.registerS2C("hackgame:host_disconnect", HostDisconnectPacket::class.java)

    // === Client to Server ===
    val DISCONNECT_C2S = PacketRegistry.registerC2S("hackgame:disconnect", DisconnectPacket::class.java)
    val PING_C2S = PacketRegistry.registerC2S("hackgame:ping", PingPacket::class.java)
    val COMMAND_C2S = PacketRegistry.registerC2S("hackgame:command", CommandPacket::class.java)
    val LOGIN_C2S = PacketRegistry.registerC2S("hackgame:login", LoginPacket::class.java)
}
