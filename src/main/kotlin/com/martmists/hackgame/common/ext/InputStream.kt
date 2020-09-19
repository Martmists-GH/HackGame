package com.martmists.hackgame.common.ext

import java.io.InputStream
import java.util.*

fun InputStream.readXBytes(len: Int): ByteArray {
    return if (len < 0) {
        throw IllegalArgumentException("len < 0")
    } else {
        var bufs: MutableList<ByteArray>? = null
        var result: ByteArray? = null
        var total = 0
        var remaining = len
        var n: Int
        do {
            val buf = ByteArray(remaining.coerceAtMost(8192))
            var nread = 0
            while (this.read(buf, nread, (buf.size - nread).coerceAtMost(remaining)).also { n = it } > 0) {
                nread += n
                remaining -= n
            }
            if (nread > 0) {
                if (2147483639 - total < nread) {
                    throw OutOfMemoryError("Required array size too large")
                }
                total += nread
                if (result == null) {
                    result = buf
                } else {
                    if (bufs == null) {
                        bufs = ArrayList<ByteArray>()
                        bufs.add(result)
                    }
                    bufs.add(buf)
                }
            }
        } while (n >= 0 && remaining > 0)
        if (bufs == null) {
            if (result == null) {
                ByteArray(0)
            } else {
                if (result.size == total) result else Arrays.copyOf(result, total)
            }
        } else {
            result = ByteArray(total)
            var offset = 0
            remaining = total
            var count: Int
            val var12 = bufs.iterator()
            while (var12.hasNext()) {
                val b = var12.next()
                count = b.size.coerceAtMost(remaining)
                System.arraycopy(b, 0, result, offset, count)
                offset += count
                remaining -= count
            }
            result
        }
    }
}