package com.martmists.client.ui

class ColorTheme {
    var BACKGROUND = 0x000000
    var DEFAULT = 0x000000

    var BLACK = 0x000000
    var RED = 0x000000
    var GREEN = 0x000000
    var YELLOW = 0x000000
    var BLUE = 0x000000
    var MAGENTA = 0x000000
    var CYAN = 0x000000
    var WHITE = 0x000000

    var BRIGHT_BLACK = 0x000000
    var BRIGHT_RED = 0x000000
    var BRIGHT_GREEN = 0x000000
    var BRIGHT_YELLOW = 0x000000
    var BRIGHT_BLUE = 0x000000
    var BRIGHT_MAGENTA = 0x000000
    var BRIGHT_CYAN = 0x000000
    var BRIGHT_WHITE = 0x000000

    companion object {
        // Some themes from https://terminal.sexy/
        fun googleDark() = ColorTheme().apply {
            BACKGROUND = 0x1d1f21
            DEFAULT = 0xc5c8c6
            BLACK = 0x1d1f21
            RED = 0xcc342b
            GREEN = 0x198844
            YELLOW = 0xfba922
            BLUE = 0x3971ed
            MAGENTA = 0xa36ac7
            CYAN = 0x3971ed
            WHITE = 0xc5c8c6
            BRIGHT_BLACK = 0x969896
            BRIGHT_RED = 0xcc342b
            BRIGHT_GREEN = 0x198844
            BRIGHT_YELLOW = 0xfba922
            BRIGHT_BLUE = 0x3971ed
            BRIGHT_MAGENTA = 0xa36ac7
            BRIGHT_CYAN = 0x3971ed
            BRIGHT_WHITE = 0xffffff
        }
    }
}
