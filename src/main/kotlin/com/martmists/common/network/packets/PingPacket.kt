package com.martmists.common.network.packets

import kotlinx.serialization.Serializable

@Serializable
data class PingPacket(val time: Long)
