package com.martmists.client.ui.components

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.TextRenderer
import com.martmists.client.ui.utils.Position
import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Paint
import kotlin.math.floor
import kotlin.math.roundToInt

abstract class TextBoxElement : BoxedElement() {
    private var wrap = false

    open val padding = 2f
    open val fontSize = 16f

    val charSize: Float
        get() {
            val renderer = TextRenderer(Canvas(1, false, null))
            val s = renderer.getSize(" ", fontSize)
            renderer.close()
            return s
        }

    fun drawString(context: RenderContext, string: String, pos: Position, paint: Paint) {
        var offset = 0
        for (c in string) {
            drawChar(context, c, pos.offset(offset, 0), paint)
            offset++
        }
    }

    private fun drawChar(context: RenderContext, char: Char, pos: Position, paint: Paint) {
        context.textRenderer.drawString(
            char.toString(),
            absolute(Position(padding + pos.x * charSize, box.height - (padding + (pos.y + 1) * fontSize))),
            paint,
            fontSize)
    }
}
