package com.martmists.hackgame.server.game

import com.martmists.hackgame.server.database.DatabaseManager
import com.martmists.hackgame.server.database.dataholders.StoredHostDevice
import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory
import com.martmists.hackgame.server.database.tables.HostTable
import com.martmists.hackgame.server.entities.SoftwareRegistry
import com.martmists.hackgame.server.game.software.Software
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.net.Inet4Address
import kotlin.random.Random

object HostManager {
    val activeHosts = mutableMapOf<String, HostDevice>()

    fun getRandomAvailableIp(): String {
        var addr: String
        do {
            addr = "${Random.nextInt(256)}.${Random.nextInt(256)}.${Random.nextInt(256)}.${Random.nextInt(256)}"
        } while (addr in activeHosts.keys)
        return addr
    }

    fun getRandomUsedIp(): String {
        return activeHosts.keys.random()
    }

    fun loadOrCreateHost(ip: String, default: StoredHostDevice): HostDevice {
        if (activeHosts.containsKey(ip)) {
            return activeHosts[ip]!!
        }

        val host = DatabaseManager.transaction {
            var host = HostTable.select { HostTable.address.eq(ip) }.firstOrNull()?.let { ProtoBuf.decodeFromByteArray<StoredHostDevice>(it[HostTable.device]) }
            if (host == null) {
                host = default
                HostTable.insert {
                    it[HostTable.address] = ip
                    it[HostTable.device] = ProtoBuf.encodeToByteArray<StoredHostDevice>(host)
                }
            }
            return@transaction host
        }.get()

        return HostDevice(ip, host.software.map { SoftwareRegistry.get(it) }, host.money, host.files).also {
            // Set as active host
            activeHosts[ip] = it
        }
    }
}
