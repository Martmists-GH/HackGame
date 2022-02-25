package com.martmists.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class HostDisconnectPacket(
        val current: String,
        val previous: String
)
