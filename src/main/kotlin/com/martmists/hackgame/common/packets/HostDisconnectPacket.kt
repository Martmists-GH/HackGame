package com.martmists.hackgame.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class HostDisconnectPacket(
        val current: String,
        val previous: String
)
