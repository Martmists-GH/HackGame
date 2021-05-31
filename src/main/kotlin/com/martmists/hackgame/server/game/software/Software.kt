package com.martmists.hackgame.server.game.software

interface Software {
    val name: String
    val tier: Int

    val type: Type

    enum class Type {
        LOCK,
        EXECUTABLE,
        MALWARE,
        TROPHY
    }
}
