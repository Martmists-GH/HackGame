package com.martmists.hackgame.common.registry

import com.martmists.hackgame.common.entities.PacketRegistry
import com.martmists.hackgame.common.packets.CommandPacket
import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.LoginPacket
import com.martmists.hackgame.common.packets.PingPacket

object BuiltinPackets {
    // === Server to Client ===
    val DISCONNECT_S2C = PacketRegistry.registerS2C("hackgame:disconnect", DisconnectPacket::class.java)
    val PING_S2C = PacketRegistry.registerS2C("hackgame:ping", PingPacket::class.java)

    // === Client to Server ===
    val DISCONNECT_C2S = PacketRegistry.registerC2S("hackgame:disconnect", DisconnectPacket::class.java)
    val PING_C2S = PacketRegistry.registerC2S("hackgame:ping", PingPacket::class.java)
    val COMMAND_C2S = PacketRegistry.registerC2S("hackgame:command", CommandPacket::class.java)
    val LOGIN_C2S = PacketRegistry.registerC2S("hackgame:login", LoginPacket::class.java)
}
