package com.martmists.hackgame.common.packets

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackPacket(val msg: String)
