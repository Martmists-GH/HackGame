package com.martmists.common.network

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

abstract class ConnectionManager {
    private val exec = Executors.newCachedThreadPool()
    private val selector = ActorSelectorManager(exec.asCoroutineDispatcher())
    val factory = aSocket(selector).tcp()

    abstract suspend fun run()
}
