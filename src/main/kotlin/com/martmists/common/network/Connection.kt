package com.martmists.common.network

import com.martmists.common.utilities.Loggable
import com.martmists.common.network.BuiltinPackets.PING
import com.martmists.common.network.packets.PingPacket
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean

abstract class Connection(protected val socket: Socket) : Loggable {
    protected var isClosed = AtomicBoolean(false)
    private val sendQueue = Channel<ByteArray>()
    abstract val dispatcher: CoroutineDispatcher

    fun CoroutineScope.spawn() {
        launch(dispatcher) {
            readLoop()
        }
        launch(dispatcher) {
            writeLoop()
            onClose()
            sendQueue.close()
        }
        launch(dispatcher) {
            pingLoop()
        }
    }

    suspend fun send(packet: ByteArray) {
        sendQueue.send(packet)
    }

    fun close() {
        isClosed.set(true)
    }

    open suspend fun onClose() {
        socket.close()
    }

    abstract suspend fun asContext(): PacketContext

    protected open suspend fun readLoop() {
        val channel = socket.openReadChannel()
        while (!isClosed.get()) {  // isClosed is an AtomicBoolean
            try {
                withTimeout(60 * 1000L) {
                    val size = channel.readIntLittleEndian()
                    val arr = ByteArray(size)
                    channel.readFully(arr)
                    PacketRegistry.receive(arr, asContext())
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

    private suspend fun writeLoop() {
        val channel = socket.openWriteChannel(true)
        while (!isClosed.get()) {
            try {
                withTimeout(60 * 1000L) {
                    val item = sendQueue.receive()
                    channel.writeIntLittleEndian(item.size)
                    channel.writeFully(item, 0, item.size)
                }
            } catch (e: TimeoutCancellationException) {
                // Allow exiting the loop if read times out
            } catch (e: ClosedSendChannelException) {
                isClosed.set(true)
                warn("Write channel closed unexpectedly")
            } catch (e: Exception) {
                error("Unhandled exception in write loop: $e")
                error(e.stackTraceToString())
            }
        }
    }

    private suspend fun pingLoop() {
        PING.send(this, PingPacket(Instant.now().toEpochMilli()))

        while (!isClosed.get()) {
            delay(30 * 1000L)
            if (sendQueue.isEmpty) {
                PING.send(this, PingPacket(Instant.now().toEpochMilli()))
            }
        }
    }
}
