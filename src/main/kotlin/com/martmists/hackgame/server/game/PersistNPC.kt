package com.martmists.hackgame.server.game

import com.martmists.hackgame.server.Server
import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory
import com.martmists.hackgame.server.entities.SoftwareRegistry
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlin.random.Random

object PersistNPC {
    val ipProviderHost = HostDevice(
        "1.2.3.4",
        listOf(),
        listOf(),
        0,
        VFSDirectory("root")
    )

    private val npc = mutableListOf<String>()

    init {

    }

    fun load() {
        HostManager.registerTempHost(ipProviderHost)
        thread(start=true, isDaemon=true, name="[AI] 1.2.3.4 AI", block=::providerAI)
    }

    fun generateRandomNPC() : HostDevice {
        val ip = HostManager.getRandomAvailableIp()
        // TODO: Software
        return HostDevice(ip, listOf(), listOf(), Random.Default.nextInt(0, 10), VFSDirectory.empty()).also {
            HostManager.registerTempHost(it)
        }
    }

    private fun providerAI() {
        while (Server.INSTANCE.running) {
            // Refresh every 15 minutes
            val now = LocalDateTime.now()
            val dir = ipProviderHost.filesystem.getOrCreateDir("logs")
            val file = dir.getOrCreateFile("users.txt")

            if (now.second == 0 && now.minute % 15 == 0) {
                npc.forEach {
                    HostManager.removeTempHost(it)
                }
                npc.clear()
                for (x in 1..50) {
                    npc.add(generateRandomNPC().ip)
                }

                file.contents = npc.joinToString("\n") { "Pinging $it: Success after ${Random.Default.nextInt(16)} tries." }
            }
        }
    }
}
