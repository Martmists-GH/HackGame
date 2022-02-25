package com.martmists.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class DisconnectPacket(val reason: String, val reconnect: Boolean)
