package com.martmists.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.time.Duration

fun CoroutineScope.cron(timeBetween: Duration, block: suspend () -> Unit) {
    launch {
        var lastTime = Instant.now()
        while (true) {
            val now = Instant.now()
            val dt = lastTime.until(now, ChronoUnit.MILLIS)
            if (dt >= timeBetween.inWholeMilliseconds) {
                block()
                lastTime = now
            } else {
                delay(dt)
            }
            block()
        }
    }
}
