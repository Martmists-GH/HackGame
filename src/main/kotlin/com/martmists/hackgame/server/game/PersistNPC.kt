package com.martmists.hackgame.server.game

import com.martmists.hackgame.server.Server
import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory
import com.martmists.hackgame.common.entities.cron
import com.martmists.hackgame.server.events.NpcEvents
import kotlin.random.Random

object PersistNPC {
    val ipProviderHost = HostDevice(
        "1.2.3.4",
        listOf(),
        listOf(),
        0,
        VFSDirectory("root"),
        "h4ckpr00f"
    )
    private val providerNPCs = mutableListOf<String>()


    fun load() {
        HostManager.registerTempHost(ipProviderHost)
        initProviderAI()
    }

    fun generateRandomNPC() : HostDevice {
        val ip = HostManager.getRandomAvailableIp()
        return HostDevice(ip, listOf(), listOf(), Random.Default.nextInt(0, 10), VFSDirectory.empty(), HostManager.generateRandomPass()).also {
            HostManager.registerTempHost(it)
        }
    }

    private fun initProviderAI() {
        cron("[AI] 1.2.3.4 Software", seconds=1) {
            // Reset password if changed
            if (ipProviderHost.password != "h4ckpr00f") {
                ipProviderHost.broadcast("don't do that.")
                ipProviderHost.password = "h4ckpr00f"
            }

            // Remove access.log if present
            val dir = ipProviderHost.filesystem.getOrCreateDir("logs")
            dir.removeFile("access.log")

            // Re-add software for AI thread

        }

        cron("[AI] 1.2.3.4 AI", minutes=15) {
            // Add NPC hosts to 1.2.3.4 logs

            val dir = ipProviderHost.filesystem.getOrCreateDir("logs")
            dir.files.filter { it.filename.startsWith("probe") }.map { dir.removeFile(it.filename) }

            val num = Server.INSTANCE.connected.size
            for (x in 0..Random.nextInt(num+1, num+3)) {
                val file = dir.getOrCreateFile("probe$x.log")

                providerNPCs.forEach {
                    NpcEvents.DESPAWN.invoker().invoke(HostManager.activeHosts[it]!!)
                    HostManager.removeTempHost(it)
                }
                providerNPCs.clear()
                for (x in 1..20) {
                    val npc = generateRandomNPC()
                    NpcEvents.SPAWN.invoker().invoke(npc)
                    providerNPCs.add(npc.ip)
                }

                file.contents = providerNPCs.joinToString("\n") { "Pinging $it: Success after ${Random.nextInt(1, 16)} tries." }
            }
        }
    }
}
