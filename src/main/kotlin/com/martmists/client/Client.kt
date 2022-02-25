package com.martmists.client

import com.martmists.client.commands.ClientCommandContext
import com.martmists.client.network.ClientConnection
import com.martmists.client.ui.GameWindow
import com.martmists.client.ui.layer.GuiLayer
import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dsl.build
import com.martmists.common.utilities.Loggable
import com.martmists.common.loadConfig
import com.martmists.common.network.BuiltinPackets
import com.martmists.common.network.ConnectionManager
import com.martmists.common.network.packets.CommandPacket
import kotlinx.coroutines.*
import java.net.ConnectException

object Client : ConnectionManager(), Loggable {
    private var running = true
    private var connection: ClientConnection? = null

    val dispatcher = Dispatcher<ClientCommandContext>()
    val config by lazy {
        loadConfig<ClientConfig>("client.yaml", "/configs/client.yaml")
    }

    fun command(input: String) = runBlocking {
        val ctx = ClientCommandContext(input)
        if (!dispatcher.dispatch(ctx)) {
            connection?.let {
                BuiltinPackets.COMMAND.send(it, CommandPacket(input))
            }
        }
    }

    private fun buildCommands() {
        build(dispatcher) {

        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        ClientPacketCallbacks.initialize()
        buildCommands()
        run()
    }

    private suspend fun nextTimeout() = sequence {
        yield(5)
        yield(10)
        yield(20)
        yield(30)

        while (true) {
            yield(60)
        }
    }

    private suspend fun createNewConnection() {
        val cfg = config.server

        while (running) {
            val dt = nextTimeout().iterator()

            coroutineScope {
                while (running) {
                    try {
                        trace("Attempting to connect to server...")
                        connection = ClientConnection(factory.connect(cfg.host, cfg.port)).apply {
                            spawn()
                        }
                        break
                    } catch (e: ConnectException) {
                        val time = dt.next()
                        warn("Could not connect to server at ${cfg.host}:${cfg.port}, retrying in ${time}s")
                        delay(time * 1000L)
                    }
                }
            }

            if (running && !connection!!.reconnect.get()) {
                break
            }
        }
    }

    override suspend fun run() {
        GlobalScope.launch {
            createNewConnection()
        }

        mainLoop()
    }

    private fun mainLoop() {
        GameWindow().also {
            it.addLayer(1000, GuiLayer())
            it.start()
        }
        running = false
        connection?.close()
    }
}
