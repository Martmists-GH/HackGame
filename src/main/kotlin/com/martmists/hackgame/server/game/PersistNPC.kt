package com.martmists.hackgame.server.game

import com.martmists.hackgame.common.entities.ActionResult
import com.martmists.hackgame.server.Server
import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory
import com.martmists.hackgame.server.events.HostEvents
import com.martmists.hackgame.server.events.NpcEvents
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
    private val providerNPCs = mutableListOf<String>()


    fun load() {
        HostManager.registerTempHost(ipProviderHost)
        thread(start=true, isDaemon=true, name="[AI] 1.2.3.4 AI", block=::providerAI)
    }

    fun generateRandomNPC() : HostDevice {
        val ip = HostManager.getRandomAvailableIp()
        return HostDevice(ip, listOf(), listOf(), Random.Default.nextInt(0, 10), VFSDirectory.empty()).also {
            HostManager.registerTempHost(it)
        }
    }

    private fun providerAI() {
        HostEvents.AFTER_CONNECT.addListener { host, source, player ->
            if (host == ipProviderHost) {
                // TODO: Add (non-installed) software files if missing
            }
            ActionResult.PASS
        }

        while (Server.INSTANCE.running) {
            // Refresh every 15 minutes
            val now = LocalDateTime.now()
            val dir = ipProviderHost.filesystem.getOrCreateDir("logs")
            dir.files.filter { it.filename.startsWith("probe") }.map { dir.removeFile(it.filename) }

            for (x in 0..Random.Default.nextInt(3, 6)) {
                val file = dir.getOrCreateFile("probe$x.log")

                if (now.second == 0 && now.minute % 15 == 0) {
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

                    file.contents = providerNPCs.joinToString("\n") { "Pinging $it: Success after ${Random.Default.nextInt(1, 16)} tries." }
                }
            }
        }
    }
}
