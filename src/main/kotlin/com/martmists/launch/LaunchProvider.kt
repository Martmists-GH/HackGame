package com.martmists.launch

import com.martmists.common.utilities.Environment
import net.fabricmc.loader.api.metadata.ModDependency
import net.fabricmc.loader.impl.game.GameProvider
import net.fabricmc.loader.impl.game.patch.GameTransformer
import net.fabricmc.loader.impl.launch.FabricLauncher
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata
import net.fabricmc.loader.impl.metadata.ModDependencyImpl
import net.fabricmc.loader.impl.util.Arguments
import java.io.File
import java.nio.file.Path
import java.util.*


class LaunchProvider : GameProvider {
    private val transformer = GameTransformer()
    private lateinit var gameJar: Path
    private lateinit var entrypoint: String
    private lateinit var arguments: Arguments

    override fun getGameId(): String {
        return gameName.replace(' ', '_').lowercase()
    }

    override fun getGameName(): String {
        return Main.game
    }

    override fun getRawGameVersion(): String {
        return Environment.gameVersion
    }

    override fun getNormalizedGameVersion(): String {
        return Environment.gameVersion
    }

    override fun getBuiltinMods(): MutableCollection<GameProvider.BuiltinMod> {
        return mutableListOf(
            GameProvider.BuiltinMod(
                listOf(gameJar),
                BuiltinModMetadata.Builder(gameId, normalizedGameVersion).setName(gameName).apply {
                    addDependency(
                        ModDependencyImpl(
                            ModDependency.Kind.DEPENDS,
                            "java",
                            listOf(">=17")
                        )
                    )
                }.build()
            )
        )
    }

    override fun getEntrypoint(): String {
        return entrypoint
    }

    private fun getLaunchDirectory(argMap: Arguments) = File(argMap.getOrDefault("gameDir", "."))

    override fun getLaunchDirectory(): Path {
        return getLaunchDirectory(arguments).toPath()
    }

    override fun isObfuscated(): Boolean {
        return false
    }

    override fun requiresUrlClassLoader(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun locateGame(launcher: FabricLauncher, args: Array<out String>): Boolean {
        val arguments = Arguments()
        this.arguments = arguments
        arguments.parse(args)

        val entrypointClasses = listOf(Main.entry)

        val entrypointResult = Helpers.findFirstClass(this::class.java.classLoader, entrypointClasses)
        if (!entrypointResult.isPresent) {
            return false
        }

        entrypoint = entrypointResult.get().entrypointName
        gameJar = entrypointResult.get().entrypointPath

        transformer.locateEntrypoints(launcher, gameJar)

        return true
    }

    override fun initialize(launcher: FabricLauncher) {

    }

    override fun getEntrypointTransformer(): GameTransformer {
        return transformer
    }

    override fun unlockClassPath(launcher: FabricLauncher) {
        launcher.addToClassPath(gameJar)
    }

    override fun launch(loader: ClassLoader) {
        try {
            val clazz = loader.loadClass(entrypoint)
            val method = clazz.getMethod("main", Array<String>::class.java)
            method.invoke(null, arguments.toArray() as Any)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun getArguments(): Arguments {
        return arguments
    }

    override fun getLaunchArguments(sanitize: Boolean): Array<String> {
        return arguments.toArray()
    }
}
