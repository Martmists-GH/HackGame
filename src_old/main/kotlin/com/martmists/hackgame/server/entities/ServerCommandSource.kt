package com.martmists.server.entities

import com.martmists.server.database.dataholders.StoredAccount
import com.martmists.server.game.HostDevice
import com.martmists.server.game.PlayerSession
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class ServerCommandSource(connection: ServerConnection) : ServerPacketContext(connection) {

}
