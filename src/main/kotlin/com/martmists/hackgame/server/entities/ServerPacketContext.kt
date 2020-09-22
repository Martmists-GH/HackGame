package com.martmists.hackgame.server.entities

import com.martmists.hackgame.common.entities.PacketContext
import com.martmists.hackgame.server.database.dataholders.StoredAccount
import com.martmists.hackgame.server.game.HostDevice
import com.martmists.hackgame.server.game.PlayerSession

open class ServerPacketContext(val connection: ServerConnection) : PacketContext {
    val account: StoredAccount
        get() = connection.session.account

    val session: PlayerSession
        get() = connection.session

    val currentHost: HostDevice
        get() = connection.session.connectChain.peek()

    val ownHost: HostDevice
        get() = connection.session.connectChain.firstElement()
}
