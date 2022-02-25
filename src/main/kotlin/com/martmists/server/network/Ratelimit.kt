package com.martmists.server.network

class Ratelimit(private val limit: Int, private val periodMs: Long) {
    private var lastHit = System.currentTimeMillis()
    private var hits = 0

    suspend fun hit() : Boolean {
        val now = System.currentTimeMillis()
        if (now - lastHit > periodMs) {
            lastHit = now
            hits = 1
        } else {
            hits++
        }

        if (hits > limit) {
            return false
        }

        return true
    }
}
