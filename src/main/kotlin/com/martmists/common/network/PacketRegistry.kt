package com.martmists.common.network

import com.martmists.common.utilities.Loggable
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer

object PacketRegistry : Loggable {
    private val typeRegistry = mutableMapOf<String, PacketTypeHandle<out Any?>>()

    @Serializable
    private data class PacketWrapper(
        val type: String,
        val data: ByteArray
    )

    class PacketTypeHandle<T>(
        private val type: String,
        private val serializer: (T) -> ByteArray,
        private val deserializer: (ByteArray) -> T,
        private var handler: (suspend PacketContext.(T) -> Unit)?
    ) : Loggable {
        fun <C : PacketContext> handler(handler: suspend C.(T) -> Unit) {
            if (this.handler != null) {
                throw IllegalStateException("Handler already set")
            }
            this.handler = handler as suspend PacketContext.(T) -> Unit
        }

        suspend fun send(connection: Connection, data: T) {
            if (type == "core:internal:ping") {
                trace("Sending ping packet")
            } else {
                debug("Sending packet of type $type")
            }
            val arr = ProtoBuf.encodeToByteArray(PacketWrapper(type, serializer(data)))
            connection.send(arr)
        }

        suspend fun accept(data: ByteArray, ctx: PacketContext) {
            if (type == "core:internal:ping") {
                trace("Received ping packet")
            } else {
                debug("Received packet of type $type")
            }
            handler?.invoke(ctx, deserializer(data))
        }
    }

    inline fun <reified T : Any> register(type: String) = register(type, T::class.java)
    inline fun <reified T : Any> register(type: String, noinline handler: suspend PacketContext.(T) -> Unit) = register(type, T::class.java, handler)

    @PublishedApi
    internal fun <T : Any> register(identifier: String, packetType: Class<T>) = registerImpl(identifier, packetType, null)
    @PublishedApi
    internal fun <T : Any> register(identifier: String, packetType: Class<T>, handler: (suspend PacketContext.(T) -> Unit)?) = registerImpl(identifier, packetType, handler)

    private fun <T : Any> registerImpl(key: String, klazz: Class<T>, acceptCallback: (suspend PacketContext.(T) -> Unit)?) : PacketTypeHandle<T> {
        return PacketTypeHandle(
            key,
            { ProtoBuf.encodeToByteArray(ProtoBuf.serializersModule.serializer(klazz), it) },
            { ProtoBuf.decodeFromByteArray(ProtoBuf.serializersModule.serializer(klazz), it) as T },
            acceptCallback
        ).also {
            debug("Registered packet type $key")
            typeRegistry[key] = it
        }
    }

    suspend fun <C : PacketContext> receive(data: ByteArray, ctx: C) {
        val wrapper = ProtoBuf.decodeFromByteArray<PacketWrapper>(data)
        typeRegistry[wrapper.type]?.accept(wrapper.data, ctx)
    }
}
