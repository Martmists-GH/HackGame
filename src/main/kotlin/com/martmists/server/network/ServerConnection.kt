package com.martmists.server.network

import com.martmists.common.network.BuiltinPackets
import com.martmists.common.network.Connection
import com.martmists.common.network.PacketContext
import com.martmists.common.network.PacketRegistry
import com.martmists.common.network.packets.DisconnectPacket
import com.martmists.common.network.packets.FeedbackPacket
import com.martmists.common.utilities.TextColor
import com.martmists.server.game.ClientSession
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.withTimeout

class ServerConnection(socket: Socket) : Connection(socket) {
    override val dispatcher = Dispatchers.Default
    private var session: ClientSession? = ClientSession(this)
    private val packetLimit = Ratelimit(10, 100)
    private val limitCheck = Ratelimit(3, 10000)

    init {
        ServerConnectionManager.addClient(session!!)
        info("New connection from ${socket.remoteAddress.hostname}")
    }

    override suspend fun onClose() {
        super.onClose()
        ServerConnectionManager.removeClient(session!!)
        session = null
    }

    override suspend fun asContext(): PacketContext {
        return ServerPacketContext(this.session!!)
    }

    override suspend fun readLoop() {
        val channel = socket.openReadChannel()
        while (!isClosed.get()) {  // isClosed is an AtomicBoolean
            try {
                withTimeout(60 * 1000L) {
                    val size = channel.readIntLittleEndian()
                    val arr = ByteArray(size)
                    channel.readFully(arr)
                    if (packetLimit.hit()) {
                        PacketRegistry.receive(arr, asContext())
                    } else {
                        if (!limitCheck.hit()) {
                            BuiltinPackets.DISCONNECT.send(this@ServerConnection, DisconnectPacket("Rate limit exceeded, disconnecting.", false))
                        } else {
                            BuiltinPackets.FEEDBACK.send(this@ServerConnection, FeedbackPacket("${TextColor.ANSI.BRIGHT_RED}Warning: Rate limit exceeded. Repeated behavior will result in disconnects or even bans."))
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                isClosed.set(true)
                warn("Connection timed out")
            } catch (e: ClosedReceiveChannelException) {
                isClosed.set(true)
                warn("Read channel closed unexpectedly")
            } catch (e: Exception) {
                error("Unhandled exception in read loop: $e")
                error(e.stackTraceToString())
            }
        }
    }
}
