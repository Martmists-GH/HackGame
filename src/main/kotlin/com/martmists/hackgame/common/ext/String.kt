package com.martmists.hackgame.common.ext

private val SGR_REGEX = Regex("\u001b\\[[\\d;]+m")

val String.textLength: Int
    get() = withoutSGRCodes().length

fun String.substringSGRAware(from: Int, to: Int = 0) : String {
    var buf = ""
    var inSgr = false
    val maxLength = if (to > 0) to - from else Int.MAX_VALUE
    var ignored = 0
    this.forEachIndexed { index, c ->
        if (inSgr) {
            ignored++
            if (c == 'm') {
                inSgr = false
            }
            buf += c
            return@forEachIndexed
        }

        if (c == '\u001b') {
            inSgr = true
            ignored++
            buf += c
        } else if (index - ignored >= from && buf.textLength < maxLength) {
            buf += c
        }
    }
    return buf
}

fun String.withoutSGRCodes(): String = replace(SGR_REGEX, "")
