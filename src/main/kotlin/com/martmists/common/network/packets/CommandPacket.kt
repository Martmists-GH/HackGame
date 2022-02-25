package com.martmists.common.network.packets

import kotlinx.serialization.Serializable

@Serializable
data class CommandPacket(
    val command: String,
)
