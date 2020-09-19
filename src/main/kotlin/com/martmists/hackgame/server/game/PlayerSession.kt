package com.martmists.hackgame.server.game

import com.martmists.hackgame.common.packets.CommandPacket
import com.martmists.hackgame.common.packets.LoginPacket

class PlayerSession {
    var isLoggedIn = false
    lateinit var account: PlayerAccount

    fun onLoginPacket(packet: LoginPacket) {
        account = PlayerAccount(packet.name, "1.2.3.4")
        isLoggedIn = true
    }

    fun onCommandPacket(packet: CommandPacket) {

    }
}