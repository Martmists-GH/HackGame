package com.martmists.server.entities

import com.martmists.server.game.software.Software
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
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
