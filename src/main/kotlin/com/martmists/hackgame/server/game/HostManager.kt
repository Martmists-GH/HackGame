package com.martmists.hackgame.server.game

import com.martmists.hackgame.server.database.DatabaseManager
import com.martmists.hackgame.server.database.dataholders.StoredHostDevice
import com.martmists.hackgame.server.database.tables.HostTable
import com.martmists.hackgame.server.entities.SoftwareRegistry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import kotlin.random.Random

@ExperimentalSerializationApi
object HostManager {
    val activeHosts = mutableMapOf<String, HostDevice>()

    fun loadStoredHosts() {
        val map = DatabaseManager.transaction {
            HostTable.selectAll().associate {
                val host = ProtoBuf.decodeFromByteArray<StoredHostDevice>(it[HostTable.device])
                it[HostTable.address] to HostDevice(
                    it[HostTable.address],
                    listOf(),
                    host.software.map(SoftwareRegistry::get),
                    host.money,
                    host.files,
                    host.password
                )
            }
        }.get()
        activeHosts.putAll(map)
    }

    fun getRandomAvailableIp(): String {
        var addr: String
        do {
            addr = "${Random.nextInt(256)}.${Random.nextInt(256)}.${Random.nextInt(256)}.${Random.nextInt(256)}"
        } while (addr in activeHosts.keys)
        return addr
    }

    fun generateRandomPass(): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8).joinToString("") { characters.random().toString() }
    }

    fun getRandomUsedIp(): String {
        return activeHosts.keys.random()
    }

    /**
     * Does not guarantee registering if already taken
     */
    fun registerTempHost(host: HostDevice) {
        activeHosts.putIfAbsent(host.ip, host)
    }

    fun removeTempHost(ip: String) {
        activeHosts.remove(ip)
    }

    /**
     * Store a device in the database
     */
    fun createStoredHost(ip: String, default: StoredHostDevice): HostDevice {
        val host = DatabaseManager.transaction {
            HostTable.insert {
                it[HostTable.address] = ip
                it[HostTable.device] = ProtoBuf.encodeToByteArray(default)
            }
        }.get()

        return HostDevice(ip, listOf(), default.software.map(SoftwareRegistry::get), default.money, default.files, default.password).also {
            // Set as active host
            activeHosts[ip] = it
        }
    }

    fun updateToDB(ip: String) {
        val host = activeHosts[ip]!!
        DatabaseManager.transaction {
            HostTable.update({ HostTable.address.eq(ip) }) {
                it[HostTable.device] = ProtoBuf.encodeToByteArray(StoredHostDevice(
                    host.money,
                    host.software.map(SoftwareRegistry::getId),
                    host.filesystem,
                    host.password
                ))
            }
        }
    }

    /**
     * Load a host from the database
     */
    fun loadOrCreateStoredHost(ip: String, default: StoredHostDevice): HostDevice {
        return activeHosts.getOrPut(ip) { createStoredHost(ip, default) }
    }
}
