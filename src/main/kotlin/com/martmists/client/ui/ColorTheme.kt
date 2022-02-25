package com.martmists.client.ui

abstract class ColorTheme {
    abstract val BACKGROUND: Int
    abstract val DEFAULT: Int

    abstract val BLACK: Int
    abstract val RED: Int
    abstract val GREEN: Int
    abstract val YELLOW: Int
    abstract val BLUE: Int
    abstract val MAGENTA: Int
    abstract val CYAN: Int
    abstract val WHITE: Int

    abstract val BRIGHT_BLACK: Int
    abstract val BRIGHT_RED: Int
    abstract val BRIGHT_GREEN: Int
    abstract val BRIGHT_YELLOW: Int
    abstract val BRIGHT_BLUE: Int
    abstract val BRIGHT_MAGENTA: Int
    abstract val BRIGHT_CYAN: Int
    abstract val BRIGHT_WHITE: Int

    companion object {
        // Some themes from https://terminal.sexy/

        val defaultDark = object : ColorTheme() {
            override val BACKGROUND     = 0x151515
            override val DEFAULT        = 0xd0d0d0

            override val BLACK          = 0x151515
            override val RED            = 0xac4142
            override val GREEN          = 0x90a959
            override val YELLOW         = 0xf4bf75
            override val BLUE           = 0x6a9fb5
            override val MAGENTA        = 0xaa759f
            override val CYAN           = 0x75b5aa
            override val WHITE          = 0xd0d0d0

            override val BRIGHT_BLACK   = 0x505050
            override val BRIGHT_RED     = 0xac4142
            override val BRIGHT_GREEN   = 0x90a959
            override val BRIGHT_YELLOW  = 0xf4bf75
            override val BRIGHT_BLUE    = 0x6a9fb5
            override val BRIGHT_MAGENTA = 0xaa759f
            override val BRIGHT_CYAN    = 0x75b5aa
            override val BRIGHT_WHITE   = 0xf5f5f5
        }

        val googleDark = object : ColorTheme() {
            override val BACKGROUND     = 0x1d1f21
            override val DEFAULT        = 0xc5c8c6

            override val BLACK          = 0x1d1f21
            override val RED            = 0xcc342b
            override val GREEN          = 0x198844
            override val YELLOW         = 0xfba922
            override val BLUE           = 0x3971ed
            override val MAGENTA        = 0xa36ac7
            override val CYAN           = 0x3971ed
            override val WHITE          = 0xc5c8c6

            override val BRIGHT_BLACK   = 0x969896
            override val BRIGHT_RED     = 0xcc342b
            override val BRIGHT_GREEN   = 0x198844
            override val BRIGHT_YELLOW  = 0xfba922
            override val BRIGHT_BLUE    = 0x3971ed
            override val BRIGHT_MAGENTA = 0xa36ac7
            override val BRIGHT_CYAN    = 0x3971ed
            override val BRIGHT_WHITE   = 0xffffff
        }
    }
}
