package com.martmists.common.network.packets

import kotlinx.serialization.Serializable

@Serializable
data class HostConnectPacket(val host: String)
