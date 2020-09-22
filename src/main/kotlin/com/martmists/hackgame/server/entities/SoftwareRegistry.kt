package com.martmists.hackgame.server.entities

import com.martmists.hackgame.server.game.software.Software

object SoftwareRegistry {
    private val softwareMap = mutableMapOf<String, Software>()

    fun register(id: String, software: Software) {
        softwareMap.putIfAbsent(id, software)
    }

    fun get(id: String): Software {
        return softwareMap[id] ?: error("AAA")
    }

    fun getId(software: Software): String {
        return softwareMap.entries.firstOrNull { it.value == software }?.key ?: error("AAAAAAA")
    }
}
