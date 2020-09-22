package com.martmists.hackgame.client

import com.googlecode.lanterna.gui2.dialogs.MessageDialog
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import com.martmists.hackgame.client.entities.ClientConnection
import com.martmists.hackgame.client.entities.NullLogger
import com.martmists.hackgame.client.ui.Screen
import kotlinx.cli.*
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointClient
import org.slf4j.LoggerFactory
import org.slf4j.impl.SimpleLogger
import java.io.OutputStream
import java.io.PrintStream
import java.net.ConnectException
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.system.exitProcess

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
    var terminal = false

    val host = "localhost"
    val port = 1337

    private enum class Mode {
        GUI,
        CLI
    }

    fun run(args: Array<String>) {
        val parser = ArgParser("hackgame client")
        val mode by parser.argument(ArgType.Choice<Mode>(), description = "Toggle between terminal mode and gui mode").optional().default(Mode.GUI)
        parser.parse(args)

        terminal = mode == Mode.CLI
        if (terminal)
            LOGGER = NullLogger("HackGame-Client")

        ClientPacketCallbacks.initialize()
        Screen.initialize()

        start()
    }

    fun start() {
        LOGGER.info("Starting Client")

        thread(start = true, isDaemon = true, name = "Network Thread Client") {
            reconnect(host, port)
        }
        Screen.start()
        // Screen exited
        connection.close()
    }

    tailrec fun reconnect(remote: String, port: Int) {
        val socket: Socket
        try {
            socket = Socket(remote, port)
        } catch (e: ConnectException) {
            // TODO: Open warning dialog on screen
            val chosen = MessageDialog.showMessageDialog(Screen.gui, "Disconnected", "Unable to connect to server. Press OK to reconnect.", MessageDialogButton.Close, MessageDialogButton.OK)
            if (chosen == MessageDialogButton.Close) {
                exitProcess(0)
            } else {
                reconnect(remote, port)
            }
            return
        }
        socket.soTimeout = 120_000  // 2 minutes
        connection = ClientConnection(socket)
        Screen.createLoginWindow()
        connection.run()
    }

    var LOGGER = LoggerFactory.getLogger("HackGame-Client")!!
}
