package com.martmists.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class PingPacket(val last: Int, val current: Int)
