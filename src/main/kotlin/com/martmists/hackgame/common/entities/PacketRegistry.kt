package com.martmists.hackgame.common.entities

import com.martmists.hackgame.client.entities.ClientPacketContext
import com.martmists.hackgame.server.entities.ServerPacketContext
import kotlinx.serialization.*
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.function.BiConsumer
import kotlin.reflect.KClass

@ExperimentalSerializationApi
object PacketRegistry {
    val funMapped = mutableMapOf<String, PacketConfig<*, *>>()

    @Serializable
    private data class PacketData(val id: String, val data: ByteArray)

    class PacketConfig<T, C>(private val id: String, private val encode: (T) -> ByteArray, private val decode: (ByteArray) -> T, private var acceptCallback: ((T, C) -> Unit)? = null) {
        fun handler(callback: BiConsumer<T, C>) = handler(callback::accept)
        fun handler(callback: (T, C) -> Unit) {
            acceptCallback = callback
        }

        fun send(data: T, connection: Connection) {
            val p = ProtoBuf.encodeToByteArray(PacketData(id, encode(data)))
            connection.sendPacket(p)
        }

        fun accept(data: ByteArray, ctx: C) {
            acceptCallback?.invoke(decode(data), ctx)
        }
    }

    fun <T> registerC2S(identifier: String, packetType: Class<T>) = registerC2S(identifier, packetType, null)
    fun <T> registerS2C(identifier: String, packetType: Class<T>) = registerS2C(identifier, packetType, null)
    fun <T> registerC2S(identifier: String, packetType: Class<T>, acceptCallback: ((T, ServerPacketContext) -> Unit)?) = register("c2s:$identifier", packetType, acceptCallback)
    fun <T> registerS2C(identifier: String, packetType: Class<T>, acceptCallback: ((T, ClientPacketContext) -> Unit)?) = register("s2c:$identifier", packetType, acceptCallback)

    @Deprecated("Avoid using this")
    fun <T, C : PacketContext> register(identifier: String, packetType: Class<T>, acceptCallback: ((T, C) -> Unit)?): PacketConfig<T, C> {
        val cfg = PacketConfig(
                identifier,
                { t -> ProtoBuf.encodeToByteArray(ProtoBuf.serializersModule.serializer(packetType) as KSerializer<T>, t) },
                { b -> ProtoBuf.decodeFromByteArray(ProtoBuf.serializersModule.serializer(packetType) as KSerializer<T>, b) },
                acceptCallback
        )
        funMapped[identifier] = cfg
        return cfg
    }

    fun accept(data: ByteArray, context: PacketContext) {
        val d = ProtoBuf.decodeFromByteArray<PacketData>(data)
        (funMapped[d.id] as PacketConfig<*, PacketContext>?)?.accept(d.data, context)
                ?: throw DataException("Unknown packet ID: ${d.id}")
    }
}
