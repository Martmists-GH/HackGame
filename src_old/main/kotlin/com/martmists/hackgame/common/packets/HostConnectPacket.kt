package com.martmists.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class HostConnectPacket(val host: String)
