package com.martmists.client.ui

import com.martmists.client.Client
import io.github.humbleui.skija.Canvas

class RenderContext(val canvas: Canvas) {
    val textRenderer = TextRenderer(canvas)

    private val themeConfig = Client.config.theme
    var theme = when (themeConfig.name) {
        "defaultDark" -> ColorTheme.defaultDark
        "googleDark" -> ColorTheme.googleDark
        "custom" -> object : ColorTheme() {
            override val BACKGROUND     = themeConfig.colors["background"]!!
            override val DEFAULT        = themeConfig.colors["default"]!!

            override val BLACK          = themeConfig.colors["black"]!!
            override val RED            = themeConfig.colors["red"]!!
            override val GREEN          = themeConfig.colors["green"]!!
            override val YELLOW         = themeConfig.colors["yellow"]!!
            override val BLUE           = themeConfig.colors["blue"]!!
            override val MAGENTA        = themeConfig.colors["magenta"]!!
            override val CYAN           = themeConfig.colors["cyan"]!!
            override val WHITE          = themeConfig.colors["white"]!!

            override val BRIGHT_BLACK   = themeConfig.colors["bright_black"]!!
            override val BRIGHT_RED     = themeConfig.colors["bright_red"]!!
            override val BRIGHT_GREEN   = themeConfig.colors["bright_green"]!!
            override val BRIGHT_YELLOW  = themeConfig.colors["bright_yellow"]!!
            override val BRIGHT_BLUE    = themeConfig.colors["bright_blue"]!!
            override val BRIGHT_MAGENTA = themeConfig.colors["bright_magenta"]!!
            override val BRIGHT_CYAN    = themeConfig.colors["bright_cyan"]!!
            override val BRIGHT_WHITE   = themeConfig.colors["bright_white"]!!
        }
        else -> error("Unknown theme")
    }
}
