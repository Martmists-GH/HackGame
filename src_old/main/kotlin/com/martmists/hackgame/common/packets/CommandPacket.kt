package com.martmists.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class CommandPacket(val cmd: String)
