package com.martmists.hackgame.server.game.software

interface Software {
    val name: String
    val tier: Int

    val isLock: Boolean
    val isExecutable: Boolean
}