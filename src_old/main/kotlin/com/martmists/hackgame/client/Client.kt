package com.martmists.client

import com.martmists.client.entities.ClientConnection
import com.martmists.client.ui.GameWindow
import com.martmists.client.ui.layers.GuiLayer
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional
import kotlinx.serialization.ExperimentalSerializationApi
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointClient
import java.net.ConnectException
import java.net.Socket
import kotlin.concurrent.thread

@ExperimentalSerializationApi
class Client {
    companion object {
        lateinit var INSTANCE: Client

        @JvmStatic
        fun main(args: Array<String>) {
            INSTANCE = Client()
            EntrypointClient.start(null, INSTANCE)
            INSTANCE.run(args)
        }
    }

    lateinit var connection: ClientConnection
    lateinit var window: GameWindow

    var terminal = false
    var hideIPs = false

    var host = "localhost"
    val port = 1337

    private enum class Mode {
        GUI,
        CLI
    }

    fun run(args: Array<String>) {
        val parser = ArgParser("hackgame client")
        val hideIP by parser.option(ArgType.Boolean, description = "Hide currently connected IP.").default(false)
        val remote by parser.option(ArgType.String, description = "Server IP").default("localhost")
        val mode by parser.argument(ArgType.Choice<Mode>(), description = "Toggle between terminal mode and gui mode").optional().default(Mode.GUI)
        parser.parse(args)

        hideIPs = hideIP
        terminal = mode == Mode.CLI
        host = remote

        ClientPacketCallbacks.initialize()
        setupWindow()

        start()
    }

    private fun setupWindow() {
        window.addLayer(1000, GuiLayer())
    }

    fun start() {
//        LOGGER.info("Starting Client")

        thread(start = true, isDaemon = true, name = "Network Thread Client") {
            reconnect(host, port)
        }

        window.start()

        connection.close()
    }

    fun reconnect(remote: String, port: Int) {
        val socket: Socket
        try {
            socket = Socket(remote, port)
        } catch (e: ConnectException) {
            // TODO: Open warning dialog on screen
//            val chosen = MessageDialog.showMessageDialog(Screen.gui, "Disconnected", "Unable to connect to server. Press OK to reconnect.", MessageDialogButton.Close, MessageDialogButton.OK)
//            if (chosen == MessageDialogButton.Close) {
//                exitProcess(0)
//            } else {
//                reconnect(remote, port)
//            }
            return
        }
        socket.soTimeout = 120_000  // 2 minutes
        connection = ClientConnection(socket)
//        Screen.createLoginWindow()
        connection.run()
    }
}
