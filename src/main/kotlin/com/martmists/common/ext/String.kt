package com.martmists.common.ext

import com.martmists.common.utilities.TextColor


private val SGR_REGEX = Regex("\u001b\\[[\\d;]+m")

private val String.textLength: Int
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

data class ColoredString(val fmt: String, val text: String)

fun String.partitionSRG(): List<ColoredString> {
    val elements = mutableListOf<ColoredString>()
    var buf = ""
    var fmt = ""
    var inSgr = false
    var ignored = 0
    this.forEachIndexed { index, c ->
        if (inSgr) {
            ignored++
            fmt += c
            if (c == 'm') {
                inSgr = false
            }
            return@forEachIndexed
        }

        if (c == '\u001b') {
            inSgr = true
            ignored++
            elements.add(ColoredString(fmt, buf))
            buf = ""
            fmt = "$c"
        } else  {
            buf += c
        }
    }

    if (buf.isNotEmpty()) {
        elements.add(ColoredString(fmt, buf))
    }

    return elements.filter { it.text != "" }
}

fun String.reset() : String {
    if (endsWith(TextColor.RESET.toString())) {
        return this
    }
    return this + TextColor.RESET.toString()
}
