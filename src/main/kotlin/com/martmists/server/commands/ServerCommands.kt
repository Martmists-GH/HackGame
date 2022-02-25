package com.martmists.server.commands

import com.martmists.commandparser.arguments.IntegerArgumentType
import com.martmists.commandparser.arguments.StringArgumentType
import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dsl.build
import com.martmists.common.network.BuiltinPackets
import com.martmists.common.network.packets.FeedbackPacket
import com.martmists.common.utilities.TextColor
import com.martmists.server.commands.groups.registerFs
import com.martmists.server.game.HostManager
import com.martmists.server.game.software.Probe

object ServerCommands {
    private val software = listOf(
        Probe.TIER_1,
    )

    fun register(dispatcher: Dispatcher<ServerCommandContext>) {
        software.forEach {
            it.apply {
                dispatcher.build()
            }
        }

        registerFs(dispatcher)

        build(dispatcher) {
            command("money") {
                action {
                    BuiltinPackets.FEEDBACK.send(
                        connection,
                        FeedbackPacket("$${TextColor.ANSI.BRIGHT_YELLOW}${session.device!!.money}${TextColor.RESET}")
                    )
                }

                literal("transfer") {
                    argument("target", IpArgumentType.ip()) { target ->
                        argument("amount", IntegerArgumentType.int(0)) { amount ->
                            action {
                                if (host.money < amount()) {
                                    BuiltinPackets.FEEDBACK.send(
                                        connection,
                                        FeedbackPacket("${TextColor.ANSI.RED}Not enough money")
                                    )
                                } else {
                                    val targetDevice = HostManager.getHost(target())
                                    targetDevice.money += amount()
                                    host.money -= amount()
                                    HostManager.updateToDB(targetDevice.ip)
                                    HostManager.updateToDB(host.ip)
                                    BuiltinPackets.FEEDBACK.send(
                                        connection,
                                        FeedbackPacket("Transferred $${amount()} to ${target()}")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            command("connect") {
                argument("target", IpArgumentType.ip()) { ip ->
                    argument("password", StringArgumentType.string()) { password ->
                        action {
                            val targetDevice = HostManager.getHost(ip())
                            if (targetDevice.password == password()) {
                                session.connectTo(targetDevice)
                            } else {
                                BuiltinPackets.FEEDBACK.send(
                                    connection,
                                    FeedbackPacket("${TextColor.ANSI.RED}Incorrect password")
                                )
                            }
                        }
                    }
                }
            }

            command("disconnect") {
                action {
                    session.disconnect()
                }
            }
        }
    }
}
