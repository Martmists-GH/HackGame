package com.martmists.hackgame.common.entities

import com.martmists.hackgame.server.Server
import java.time.LocalDateTime
import kotlin.concurrent.thread

fun cron(name: String, hours: Int? = null, minutes: Int? = null, seconds: Int? = null, callback: () -> Unit) {
    thread(start=true, isDaemon=true, name=name) {
        while (Server.INSTANCE.running) {
            val now = LocalDateTime.now()
            if (
                (hours == null || now.hour % hours == 0) &&
                (minutes == null || now.minute % minutes == 0) &&
                (seconds == null || now.second % seconds == 0)
            ) {
                callback()
            }
        }
    }
}
