package com.martmists.server.commands

import com.martmists.commandparser.arguments.ArgumentType
import com.martmists.common.commands.CommandContext

class IpArgumentType<C : CommandContext> private constructor() : ArgumentType<C, String>() {

    override suspend fun parse(context: C, input: String): String? {
        val ip = input.split(" ").firstOrNull() ?: return null
        val parts = ip.split(".")
        if (parts.size !in 4..6) return null
        for (part in parts) {
            if (part.length > 3) return null
            if (part.toIntOrNull() == null) return null
            if (part.toInt() > 255) return null
        }
        return ip
    }

    override suspend fun value(context: C, value: String): String {
        return value
    }

    companion object {
        fun <C : CommandContext> ip() = IpArgumentType<C>()
    }
}
