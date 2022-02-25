package com.martmists.common.network

import com.martmists.common.network.packets.*

object BuiltinPackets {
    internal val PING = PacketRegistry.register<PingPacket>("core:internal:ping") { }

    // Client to Server
    val LOGIN = PacketRegistry.register<LoginPacket>("game:c2s:login")
    val COMMAND = PacketRegistry.register<CommandPacket>("game:c2s:command")

    // Server to Client
    val DISCONNECT = PacketRegistry.register<DisconnectPacket>("game:s2c:disconnect")
    val FEEDBACK = PacketRegistry.register<FeedbackPacket>("game:s2c:feedback")
    val HOST_CONNECT = PacketRegistry.register<HostConnectPacket>("game:s2c:host_connect")
    val HOST_DISCONNECT = PacketRegistry.register<HostDisconnectPacket>("game:s2c:host_disconnect")
    val BROADCAST = PacketRegistry.register<BroadcastPacket>("game:s2c:broadcast")
}
