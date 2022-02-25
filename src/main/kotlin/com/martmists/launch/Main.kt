package com.martmists.launch

import com.martmists.common.utilities.Environment
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional
import kotlinx.cli.vararg
import net.fabricmc.loader.impl.launch.knot.KnotClient
import net.fabricmc.loader.impl.launch.knot.KnotServer

object Main {
    var isClient = false
    var entry = ""
    var game = "Hackgame"

    private enum class Mode {
        CLIENT,
        SERVER
    }

    @JvmStatic
    fun main(prgm_args: Array<String>) {
        var type = Environment.gameType
        if (type == null) {
            type = "*"  // Dev mode
        }

        val new_mode: Mode
        val new_args: Array<String>

        if (type == "*") {
            val parser = ArgParser("launch")
            val mode by parser.argument(ArgType.Choice<Mode>(), description = "Side to launch")
            val args by parser.argument(ArgType.String, "").vararg().optional()
            parser.parse(prgm_args)
            new_mode = mode
            new_args = args.toTypedArray()
        } else {
            new_mode = Mode.valueOf(type.uppercase())
            new_args = prgm_args
        }

        // Invoke entrypoint
        isClient = new_mode == Mode.CLIENT
        if (isClient) {
            entry = "com.martmists.client.Client"
//            game = "Game Client"
            KnotClient.main(new_args)
        } else {
            entry = "com.martmists.server.Server"
//            game = "Game Server"
            KnotServer.main(new_args)
        }
    }
}
