package com.martmists.server.commands

import com.martmists.common.commands.CommandContext
import com.martmists.server.game.ClientSession
import com.martmists.server.game.HostDevice
import com.martmists.server.network.ServerConnection

class ServerCommandContext(input: String, val session: ClientSession) : CommandContext(input) {
    val connection: ServerConnection
        get() = session.connection

    val host: HostDevice
        get() = session.device!!
}
