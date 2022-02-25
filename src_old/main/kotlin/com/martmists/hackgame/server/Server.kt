package com.martmists.server

import com.martmists.server.database.DatabaseManager
import com.martmists.server.entities.ServerCommandSource
import com.martmists.server.entities.ServerConnection
import com.martmists.server.events.ServerLifecycleEvents
import com.martmists.server.game.HostManager
import com.martmists.server.game.PersistNPC
import com.mojang.brigadier.CommandDispatcher
import kotlinx.serialization.ExperimentalSerializationApi
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointServer
import org.slf4j.LoggerFactory
import java.io.EOFException
import java.net.ServerSocket
import kotlin.concurrent.thread

@ExperimentalSerializationApi
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
        DatabaseManager.registerDefaultTables()
        HostManager.loadStoredHosts()
        PersistNPC.load()

//        LOGGER.info("Starting Server")

        // TODO: Event/Task thread

        // Handle these on main thread
        ServerLifecycleEvents.START.invoker().invoke(this)
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
//            LOGGER.info("Client connected from ${client.inetAddress.hostAddress}")
            connected.add(connection)
            thread(start = true, isDaemon=true, name = "Server Connection Handler #$connectionId", block = connection::run)
            connectionId += 1
        }
    }
}

