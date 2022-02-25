package com.martmists.common.network.packets

import kotlinx.serialization.Serializable

@Serializable
data class LoginPacket(
    val username: String,
    val password: String,
    val register: Boolean
)
