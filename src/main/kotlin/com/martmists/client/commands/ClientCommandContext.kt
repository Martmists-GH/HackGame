package com.martmists.client.commands

import com.martmists.client.Client
import com.martmists.client.network.ClientConnection
import com.martmists.common.commands.CommandContext

class ClientCommandContext(input: String) : CommandContext(input) {
    val connection: ClientConnection
        get() = Client.connection ?: error("Not connected to server")
}
