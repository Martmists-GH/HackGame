package com.martmists.hackgame.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class PingPacket(val last: Int, val current: Int)