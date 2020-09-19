package com.martmists.hackgame.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class CommandPacket(val cmd: String)
