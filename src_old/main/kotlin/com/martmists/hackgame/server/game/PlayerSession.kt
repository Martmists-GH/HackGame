package com.martmists.server.game

import com.martmists.common.DisconnectException
import com.martmists.common.api.ActionResult
import com.martmists.common.api.TextColor
import com.martmists.common.packets.*
import com.martmists.common.BuiltinPackets
import com.martmists.server.Server
import com.martmists.server.database.DatabaseManager
import com.martmists.server.database.dataholders.StoredAccount
import com.martmists.server.database.dataholders.StoredHostDevice
import com.martmists.server.database.dataholders.vfs.VFSDirectory
import com.martmists.server.database.tables.AccountTable
import com.martmists.server.entities.ServerCommandSource
import com.martmists.server.entities.ServerConnection
import com.martmists.server.events.HostEvents
import com.martmists.server.events.PlayerLifecycleEvents
import com.toxicbakery.bcrypt.Bcrypt
import kotlinx.serialization.ExperimentalSerializationApi
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

@ExperimentalSerializationApi
class PlayerSession(val connection: ServerConnection) {
    var isLoggedIn = false
    val connectChain = Stack<HostDevice>()
    var currentIP = ""
    var account = StoredAccount(" ", "")

    fun onLoginPacket(packet: LoginPacket) {
        if (Server.INSTANCE.connected.any { it.session.account.name == packet.name }) {
            // Already logged in
            BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("User already logged in on different connection", true), connection)
            throw DisconnectException()
        }

        val host: HostDevice
        if (packet.register) {
            val address = HostManager.getRandomAvailableIp()

            val foundAccounts = DatabaseManager.transaction {
                AccountTable.select { AccountTable.username eq packet.name }.map { it[AccountTable.username] }
            }.get()

            if (foundAccounts.isNotEmpty()) {
                BuiltinPackets.DISCONNECT_S2C.send(DisconnectPacket("Invalid login.", true), connection)
                throw DisconnectException()
            }

            host = HostManager.createStoredHost(address, StoredHostDevice(0, listOf(), VFSDirectory.empty(), HostManager.generateRandomPass()))
            PlayerLifecycleEvents.REGISTER.invoker().invoke(packet.name, host)

            DatabaseManager.transaction {
                AccountTable.insert {
                    it[AccountTable.username] = packet.name
                    it[AccountTable.passwordHash] = Bcrypt.hash(packet.password, 10)
                    it[AccountTable.homeAddress] = address
                }
            }

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
            host = HostManager.loadOrCreateStoredHost(account.homeIP, StoredHostDevice(0, listOf(), VFSDirectory.empty(), HostManager.generateRandomPass()))
            PlayerLifecycleEvents.LOGIN.invoker().invoke(packet.name, host)
        }

        connectChain.push(host)
        BuiltinPackets.HOST_CONNECT_S2C.send(HostConnectPacket(account.homeIP), connection)
        currentIP = account.homeIP
        isLoggedIn = true
    }

    fun onCommandPacket(packet: CommandPacket) {
        val source = ServerCommandSource(connection)
        val parsed = Server.INSTANCE.dispatcher.parse(packet.cmd, source)

        Server.INSTANCE.dispatcher.execute(parsed)
    }

    fun connectTo(remoteIp: String) {
        // TODO: Check if IP allows login or something idk
        if (HostManager.activeHosts.containsKey(remoteIp)) {
            if (connectChain.firstElement().ip == remoteIp) {
                BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.RED}ERROR: Cannot connect to root host"), connection)
                return
            }

            if (connectChain.lastElement().ip == remoteIp) {
                BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.RED}ERROR: Cannot connect to same host"), connection)
                return
            }

            val currentHost = connectChain.lastElement()
            val toHost = HostManager.activeHosts[remoteIp]!!

            if (HostEvents.BEFORE_CONNECT.invoker().invoke(currentHost, toHost, this) == ActionResult.FAIL) {
                return
            }

            currentIP = remoteIp
            connectChain.push(toHost)
            toHost.logConnection(currentHost.ip)
            BuiltinPackets.HOST_CONNECT_S2C.send(HostConnectPacket(remoteIp), connection)
            HostEvents.AFTER_CONNECT.invoker().invoke(currentHost, toHost, this)

        } else {
            BuiltinPackets.FEEDBACK_S2C.send(FeedbackPacket("${TextColor.ANSI.RED}ERROR: No such host: $remoteIp"), connection)
        }
    }
}
