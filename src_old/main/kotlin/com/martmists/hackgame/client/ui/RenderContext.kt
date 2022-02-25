package com.martmists.client.ui

import io.github.humbleui.skija.Canvas

class RenderContext(val canvas: Canvas) {
    val textRenderer = TextRenderer(canvas)
    var theme = ColorTheme.googleDark()  // TODO: Load from settings
}
