package com.martmists.server.entities

import com.martmists.common.internal.PacketContext
import com.martmists.server.database.dataholders.StoredAccount
import com.martmists.server.game.HostDevice
import com.martmists.server.game.PlayerSession
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
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
