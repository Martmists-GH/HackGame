package com.martmists.hackgame.common.registry

import com.martmists.hackgame.common.entities.PacketRegistry
import com.martmists.hackgame.common.packets.CommandPacket
import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.LoginPacket
import com.martmists.hackgame.common.packets.PingPacket

object BuiltinPackets {
    // === Server to Client ===
    val DISCONNECT_S2C = PacketRegistry.registerS2C<DisconnectPacket>("hackgame:disconnect")
    val PING_S2C = PacketRegistry.registerS2C<PingPacket>("hackgame:ping")

    // === Client to Server ===
    val DISCONNECT_C2S = PacketRegistry.registerC2S<DisconnectPacket>("hackgame:disconnect")
    val PING_C2S = PacketRegistry.registerC2S<PingPacket>("hackgame:ping")
    val COMMAND_C2S = PacketRegistry.registerC2S<CommandPacket>("hackgame:command")
    val LOGIN_C2S = PacketRegistry.registerC2S<LoginPacket>("hackgame:login")
}
