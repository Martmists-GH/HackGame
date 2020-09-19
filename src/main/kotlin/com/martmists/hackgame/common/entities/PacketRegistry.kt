package com.martmists.hackgame.common.entities

import com.martmists.hackgame.client.entities.ClientPacketContext
import com.martmists.hackgame.server.entities.ServerPacketContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

object PacketRegistry {
    val funMapped = mutableMapOf<String, PacketConfig<*, *>>()

    @Serializable
    private data class PacketData(val id: String, val data: ByteArray)

    class PacketConfig<T, C>(private val id: String, private val encode: (T) -> ByteArray, private val decode: (ByteArray) -> T, private var acceptCallback: ((T, C) -> Unit)? = null) {
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

    inline fun <reified T> registerC2S(identifier: String) = registerC2S<T>(identifier, null)
    inline fun <reified T> registerS2C(identifier: String) = registerS2C<T>(identifier, null)
    inline fun <reified T> registerC2S(identifier: String, noinline acceptCallback: ((T, ServerPacketContext) -> Unit)?) = register("c2s:$identifier", acceptCallback)
    inline fun <reified T> registerS2C(identifier: String, noinline acceptCallback: ((T, ClientPacketContext) -> Unit)?) = register("s2c:$identifier", acceptCallback)

    @Deprecated("Avoid using this")
    inline fun <reified T, C : PacketContext> register(identifier: String, noinline acceptCallback: ((T, C) -> Unit)?): PacketConfig<T, C> {
        val cfg = PacketConfig(identifier, { t -> ProtoBuf.encodeToByteArray(t) }, { b -> ProtoBuf.decodeFromByteArray(b) }, acceptCallback)
        funMapped[identifier] = cfg
        return cfg
    }

    fun accept(data: ByteArray, context: PacketContext) {
        val d = ProtoBuf.decodeFromByteArray<PacketData>(data)
        (funMapped[d.id] as PacketConfig<*, PacketContext>?)?.accept(d.data, context)
                ?: throw DataException("Unknown packet ID: ${d.id}")
    }
}
