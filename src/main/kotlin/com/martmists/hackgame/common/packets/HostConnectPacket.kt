package com.martmists.hackgame.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class HostConnectPacket(val host: String)
