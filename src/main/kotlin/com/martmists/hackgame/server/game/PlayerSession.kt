package com.martmists.hackgame.server.game

import com.martmists.hackgame.common.DisconnectException
import com.martmists.hackgame.common.packets.CommandPacket
import com.martmists.hackgame.common.packets.DisconnectPacket
import com.martmists.hackgame.common.packets.LoginPacket
import com.martmists.hackgame.common.registry.BuiltinPackets
import com.martmists.hackgame.server.Server
import com.martmists.hackgame.server.database.DatabaseManager
import com.martmists.hackgame.server.database.dataholders.StoredAccount
import com.martmists.hackgame.server.database.dataholders.StoredHostDevice
import com.martmists.hackgame.server.database.dataholders.vfs.VFSDirectory
import com.martmists.hackgame.server.database.tables.AccountTable
import com.martmists.hackgame.server.database.tables.HostTable
import com.martmists.hackgame.server.entities.ServerCommandSource
import com.martmists.hackgame.server.entities.ServerConnection
import com.toxicbakery.bcrypt.Bcrypt
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

class PlayerSession(val connection: ServerConnection) {
    var isLoggedIn = false
    val connectChain = Stack<HostDevice>()
    lateinit var currentIP: String
    lateinit var account: StoredAccount

    fun onLoginPacket(packet: LoginPacket) {
        if (packet.register) {
            val address = HostManager.getRandomAvailableIp()

            DatabaseManager.transaction {
                HostTable.insert {
                    it[HostTable.address] = address
                    it[HostTable.device] = ProtoBuf.encodeToByteArray<StoredHostDevice>(StoredHostDevice(0, listOf(), VFSDirectory.empty()))
                }

                AccountTable.insert {
                    it[AccountTable.username] = packet.name
                    it[AccountTable.passwordHash] = Bcrypt.hash(packet.password, 10)
                    it[AccountTable.homeAddress] = address
                }
            }.exceptionally {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("Invalid login.", true), connection)
                null
            }.get() ?: run { throw DisconnectException() }

            account = StoredAccount(packet.name, address)
        } else {
            val foundAccount = DatabaseManager.transaction {
                return@transaction AccountTable.select { AccountTable.username eq packet.name }.firstOrNull { Bcrypt.verify(packet.password, it[AccountTable.passwordHash]) }?.let { StoredAccount(packet.name, it[AccountTable.homeAddress]) }
            }.get()

            if (foundAccount == null) {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("Invalid login.", true), connection)
                throw DisconnectException()
            }

            account = foundAccount
        }

        val host = HostManager.loadOrCreateStoredHost(account.homeIP, StoredHostDevice(0, listOf(), VFSDirectory.empty()))
        connectChain.push(host)
        // TODO: HostConnectS2C packet
        currentIP = account.homeIP
        isLoggedIn = true
    }

    fun onCommandPacket(packet: CommandPacket) {
        val source = ServerCommandSource(connection)
        val parsed = Server.INSTANCE.dispatcher.parse(packet.cmd, source)
        Server.INSTANCE.dispatcher.execute(parsed)
    }

    fun connectTo(remoteIp: String) {
        // TODO: Check if IP allows login
        if (HostManager.activeHosts.containsKey(remoteIp)) {
            currentIP = remoteIp
            connectChain.push(HostManager.activeHosts[remoteIp]!!)
            // TODO: HostConnectS2C packet
        }
    }
}