package com.martmists.hackgame.common.entities

import com.googlecode.lanterna.gui2.dialogs.MessageDialog
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import com.martmists.hackgame.client.Client
import com.martmists.hackgame.client.ui.Screen
import com.martmists.hackgame.common.ext.readXBytes
import com.martmists.hackgame.loader.Main
import org.slf4j.Logger
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread
import kotlin.system.exitProcess

abstract class Connection {
    abstract val socket: Socket
    abstract val reader: InputStream
    abstract val writer: OutputStream
    abstract val logger: Logger
    abstract var connected: Boolean

    private val packetQueue = LinkedBlockingQueue<ByteArray>()
    private var flush = false

    fun runWriteThread() = thread(start=true, isDaemon=true, name="Packet Send Thread") {
        try {
            while ((connected && socket.isConnected) || flush) {
                val packet = packetQueue.take()
                writer.write(ByteBuffer.allocate(4).putInt(packet.size).array())
                writer.write(packet)
                writer.flush()
            }
        } catch (e: SocketException) {
            connected = false
        }
    }

    fun readPacket(): ByteArray {
        return try {
            val size = ByteBuffer.wrap(reader.readXBytes(4)).int
            val data = reader.readXBytes(size)
            logger.debug("Received packet of size $size: ${data.asList()}")
            if (data.isEmpty()) {
                // Disconnect
                throw EOFException()
            }
            data
        } catch(e: Exception) {
            logger.warn("Remote stopped responding")
            close()
            ByteArray(0)
        }
    }

    fun sendPacket(data: ByteArray) {
        logger.debug("Sending packet of size ${data.size}: ${data.asList()}")
        packetQueue.add(data)
    }

    open fun close() {
        connected = false
        flush = true
    }
}
