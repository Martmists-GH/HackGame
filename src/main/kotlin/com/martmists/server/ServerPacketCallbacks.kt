package com.martmists.server

import com.martmists.common.network.BuiltinPackets
import com.martmists.common.network.packets.DisconnectPacket
import com.martmists.common.network.packets.FeedbackPacket
import com.martmists.common.utilities.TextColor
import com.martmists.server.commands.ServerCommandContext
import com.martmists.server.database.dataholders.StoredHostDevice
import com.martmists.server.database.tables.AccountTable
import com.martmists.server.database.transaction
import com.martmists.server.game.HostManager
import com.martmists.server.game.vfs.VFSDirectory
import com.martmists.server.network.ServerPacketContext
import de.mkammerer.argon2.Argon2Factory
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object ServerPacketCallbacks {
    val argon2 = Argon2Factory.create()

    init {
        BuiltinPackets.LOGIN.handler<ServerPacketContext> { packet ->
            if (session.isLoggedIn || session.device != null) {
                BuiltinPackets.DISCONNECT.send(connection, DisconnectPacket("${TextColor.ANSI.RED}You are already logged in!", false))
                connection.close()
                return@handler
            }

            if (packet.register) {
                transaction {
                    if (AccountTable.select { AccountTable.username eq packet.username }.count() > 0) {
                        BuiltinPackets.DISCONNECT.send(connection, DisconnectPacket("${TextColor.ANSI.RED}Username already taken", false))
                        connection.close()
                    } else {
                        val ip = HostManager.getRandomAvailableIp()
                        val device = StoredHostDevice(
                            100,
                            listOf(),
                            VFSDirectory(),
                            HostManager.generateRandomPass()
                        )
                        val host = HostManager.createStoredHost(ip, device)
                        session.isLoggedIn = true
                        session.connectTo(host)

                        AccountTable.insert {
                            it[AccountTable.username] = packet.username
                            it[AccountTable.password] = argon2.hash(12, 65536, 1, packet.password.toCharArray())
                            it[AccountTable.homeAddress] = ip
                        }

                        BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("Account created"))
                    }
                }
            } else {
                transaction {
                    val account = AccountTable.select { AccountTable.username eq packet.username }.firstOrNull()
                    if (account == null) {
                        BuiltinPackets.DISCONNECT.send(connection, DisconnectPacket("${TextColor.ANSI.RED}Invalid username", false))
                        connection.close()
                    } else {
                        if (argon2.verify(account[AccountTable.password], packet.password.toCharArray())) {
                            val host = HostManager.getHost(account[AccountTable.homeAddress])
                            session.isLoggedIn = true
                            session.connectTo(host)
                            BuiltinPackets.FEEDBACK.send(connection, FeedbackPacket("Login successful"))
                        } else {
                            BuiltinPackets.DISCONNECT.send(connection, DisconnectPacket("${TextColor.ANSI.RED}Invalid password", false))
                            connection.close()
                        }
                    }
                }
            }
        }

        BuiltinPackets.COMMAND.handler<ServerPacketContext> {
            if (!session.isLoggedIn) {
                return@handler
            }

            val ctx = ServerCommandContext(it.command, session)
            try {
                if (!Server.dispatcher.dispatch(ctx)) {
                    throw Exception("Command not found")
                }
            } catch (e: Exception) {
                BuiltinPackets.FEEDBACK.send(session.connection, FeedbackPacket("${TextColor.ANSI.RED}${e::class.java.simpleName}: ${e.message}"))
            }
        }
    }

    fun initialize() { }
}
