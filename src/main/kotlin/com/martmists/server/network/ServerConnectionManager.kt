package com.martmists.server.network

import com.martmists.common.utilities.Loggable
import com.martmists.common.network.ConnectionManager
import com.martmists.server.Server
import com.martmists.server.game.ClientSession
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private object ServerConnectionManager : ConnectionManager(), Loggable {
    private val clients = mutableListOf<ClientSession>()

    fun addClient(client: ClientSession) {
        clients.add(client)
    }

    fun removeClient(client: ClientSession) {
        clients.remove(client)
    }

    override suspend fun run() {
        info("Waiting for connections...")
        val server = factory.bind("0.0.0.0", Server.config.server.port)

        coroutineScope {
            launch {
                mainLoop()
            }

            while (true) {
                val socket = server.accept()
                ServerConnection(socket).apply {
                    spawn()
                }
            }
        }
    }

    private suspend fun mainLoop() {
        // TODO
    }
}
