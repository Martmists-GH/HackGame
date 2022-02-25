package com.martmists.common.utilities

/**
 * Color codes
 * TODO: Make color codes work in official client
 */
class TextColor(private val text: String) {
    override fun toString() = text

    object ANSI {
        val BLACK = TextColor("\u001b[30m")
        val RED = TextColor("\u001b[31m")
        val GREEN = TextColor("\u001b[32m")
        val YELLOW = TextColor("\u001b[33m")
        val BLUE = TextColor("\u001b[34m")
        val MAGENTA = TextColor("\u001b[35m")
        val CYAN = TextColor("\u001b[36m")
        val WHITE = TextColor("\u001b[37m")

        val BRIGHT_BLACK = TextColor("\u001b[30;1m")
        val BRIGHT_RED = TextColor("\u001b[31;1m")
        val BRIGHT_GREEN = TextColor("\u001b[32;1m")
        val BRIGHT_YELLOW = TextColor("\u001b[33;1m")
        val BRIGHT_BLUE = TextColor("\u001b[34;1m")
        val BRIGHT_MAGENTA = TextColor("\u001b[35;1m")
        val BRIGHT_CYAN = TextColor("\u001b[36;1m")
        val BRIGHT_WHITE = TextColor("\u001b[37;1m")
    }

    companion object {
        val RESET = TextColor("\u001b[0m")

        fun fromRGB(r: Int, g: Int, b: Int) = TextColor("\u001b[38;2;$r;$g;${b}m")
    }
}
