package com.martmists.hackgame.loader

import com.jcabi.manifests.Manifests
import com.martmists.hackgame.client.Client
import com.martmists.hackgame.server.Server
import kotlinx.cli.*
import net.fabricmc.loader.game.GenericApplicationProvider
import net.fabricmc.loader.launch.common.FabricLauncherBase
import net.fabricmc.loader.launch.knot.KnotClient
import net.fabricmc.loader.launch.knot.KnotServer
import org.slf4j.LoggerFactory
import org.spongepowered.asm.service.IMixinService
import org.spongepowered.asm.service.MixinService
import java.util.*

object Main {
    var isClient = false

    private enum class Mode {
        CLIENT,
        SERVER
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val devLogger = LoggerFactory.getLogger("HackGame-Dev")

        val version = try {
            Manifests.read("Game-Type")
        } catch (e: IllegalArgumentException) {
            devLogger.warn("No value found for Game-Type. If you are not running in dev, something is very wrong!")
            null
        }

        val mode: Mode
        val new_args: Array<String>
        if (version == null || version == "*") {
            val parser = ArgParser("hackgame")
            val _mode by parser.argument(ArgType.Choice<Mode>(), description = "Side to launch")
            val _args by parser.argument(ArgType.String, "").vararg().optional()
            parser.parse(args)
            mode = _mode
            new_args = _args.toTypedArray()
        } else {
            mode = Mode.valueOf(version.toUpperCase())
            new_args = args
        }

        // Invoke entrypoints
        isClient = mode == Mode.CLIENT
        if (isClient) {
            GenericApplicationProvider("com.martmists.hackgame.client.Client", arrayOf(), "HackGame Client")
            KnotClient.main(new_args)
        } else {
            GenericApplicationProvider("com.martmists.hackgame.server.Server", arrayOf(), "HackGame Server")
            KnotServer.main(new_args)
        }
    }
}