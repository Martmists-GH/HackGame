package com.martmists.hackgame.common.packets

import kotlinx.serialization.Serializable

@Serializable
class LoginPacket(val name: String,
                  val password: String,
                  val register: Boolean)
