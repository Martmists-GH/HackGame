package com.martmists.client.ui

import com.martmists.client.ui.utils.Position
import io.github.humbleui.skija.*
import java.io.File

class TextRenderer(val canvas: Canvas) {
    private val fontCache = mutableMapOf<Float, Font>()

    private val typeface: Typeface

    init {
        val uri = this::class.java.classLoader.getResource("game_font.ttf")!!.toURI()
        val bytes = File(uri).readBytes()
        val data = Data.makeFromBytes(bytes)
        typeface = Typeface.makeFromData(data)
        data.close()
    }

    fun getFont(size: Float): Font {
        return fontCache.getOrPut(size) {
            Font(typeface, size)
        }
    }

    fun getSize(text: String, size: Float) : Float {
        return getFont(size).measureTextWidth(text)
    }

    fun drawString(text: String, pos: Position, paint: Paint, size: Float) {
        canvas.drawString(text, pos.x, pos.y - size, getFont(size), paint)
    }

    fun close() {
        fontCache.forEach { t, u ->
            u.close()
        }
    }
}
