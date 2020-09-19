package com.martmists.hackgame.server

import com.martmists.hackgame.server.entities.ServerCommandSource
import com.martmists.hackgame.server.entities.ServerConnection
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointServer
import org.slf4j.LoggerFactory
import java.io.EOFException
import java.net.ServerSocket
import kotlin.concurrent.thread

class Server {
    companion object {
        lateinit var INSTANCE: Server

        @JvmStatic
        fun main(args: Array<String>) {
            INSTANCE = Server()
            EntrypointServer.start(null, INSTANCE)
            INSTANCE.run(args)
        }
    }

    var port = 1337
    var connectionId = 0

    @Volatile
    var running = true
    val connected = mutableListOf<ServerConnection>()
    lateinit var dispatcher: CommandDispatcher<ServerCommandSource>

    fun run(args: Array<String>) {
        // Do initialization of stuffs n things
        dispatcher = CommandDispatcher<ServerCommandSource>()
        ServerCommands.initialize(dispatcher)
        ServerPacketCallbacks.initialize()
        LOGGER.info("Starting Server")

        // TODO: Event/Task thread

        // Handle these on main thread
        handleConnections()
    }

    fun handleConnections() {
        val server = ServerSocket(port)

        while (running) {
            val client = server.accept()
            client.soTimeout = 5_000  // 5 seconds
            val connection: ServerConnection
            try {
                connection = ServerConnection(client)
            } catch (e: EOFException) {
                client.close()
                continue
            }
            client.soTimeout = 120_000  // 2 minutes
            LOGGER.info("Client connected from ${client.inetAddress.hostAddress}")
            connected.add(connection)
            thread(start = true, isDaemon=true, name = "Server Connection Handler #$connectionId", block = connection::run)
            connectionId += 1
        }
    }

    val LOGGER = LoggerFactory.getLogger("HackGame-Server")!!
}

