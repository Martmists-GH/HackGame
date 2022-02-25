package com.martmists.client.ui.element

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.TextRenderer
import com.martmists.client.ui.shape.Position
import com.martmists.common.ext.partitionSRG
import com.martmists.common.ext.withoutSGRCodes
import com.martmists.common.utilities.Loggable
import com.martmists.common.utilities.TextColor
import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Paint
import kotlin.math.floor
import kotlin.math.roundToInt

open class TextElement(content: String, private val scrollable: Boolean = false) : Element(), Loggable {
    private var realText = ""
    private var buffer = ""

    private var scrollOffset = 0
    private val scrollMax: Int
        get() = (buffer.count { it == '\n' } - heightChars).coerceAtLeast(0)

    private var prev = false
    protected val needsScrollbar: Boolean
        get() = scrollable && scrollMax > 0 && heightChars >= 4

    protected fun needsUpdateScrollbar() : Boolean {
        val res = needsScrollbar != prev
        prev = needsScrollbar
        return res
    }

    companion object {
        const val padding = 2f
        const val fontSize = 16f
        val charSize: Float by lazy {
            val renderer = TextRenderer(Canvas(1, false, null))
            val s = renderer.getSize(" ", fontSize)
            renderer.close()
            s
        }
    }

    protected fun drawString(context: RenderContext, string: String, pos: Position, paint: Paint) {
        var offset = 0
        for (c in string) {
            drawChar(context, c, Position(pos.x + offset, pos.y), paint)
            offset++
        }
    }

    private fun drawChar(context: RenderContext, char: Char, pos: Position, paint: Paint) {
        val newPos = Position(bounds.x + padding + pos.x * charSize, bounds.y + (padding + (pos.y + 2) * fontSize))
        context.textRenderer.drawString(
            char.toString(),
            newPos,
            paint,
            fontSize
        )
    }

    protected val widthChars: Int
        get() = floor(bounds.width / charSize).roundToInt()
    protected val heightChars: Int
        get() = floor(bounds.height / fontSize).roundToInt()

    init {
        content.split('\n').forEach {
            addLine(it)
        }
    }

    override fun render(ctx: RenderContext) {
        val onScreen = buffer.split('\n').let {
            it.subList((it.size-heightChars-2).coerceAtLeast(0), (scrollOffset + heightChars).coerceAtMost(it.size)).joinToString("\n")
        }
        onScreen.split('\n').forEachIndexed { index, line ->
            var x = 0
            for (part in line.partitionSRG()) {
                getPaint(ctx, part.fmt).use {
                    drawString(ctx, part.text, Position(x.toFloat(), index), it)
                }
                x += part.text.length
            }
        }
        if (needsScrollbar) {
            val progress = scrollOffset / scrollMax.toFloat()
            val barSize = (1f / scrollMax) * heightChars

            for (i in 0 until heightChars) {
                val pos = Position(bounds.width - padding - 1, bounds.y + padding + i * fontSize)
                val paint = getPaint(ctx, TextColor.RESET.toString())
                val c = when(i.toFloat()) {
                    0f -> "^"
                    in (progress * heightChars).rangeTo((progress * heightChars) + barSize) -> "="
                    heightChars - 1f -> "v"
                    else -> "|"
                }
                drawString(ctx, c, pos, paint)
            }
        }
    }

    fun addLine(line: String) = addLine(line, true)
    open fun addLine(text: String, addToReal: Boolean, causedByUpdate: Boolean = false) {
        if (addToReal) {
            if (realText.isBlank()) {
                realText = text
            } else {
                realText += "\n$text"
            }

            if (!causedByUpdate && needsUpdateScrollbar()) {
                realOnUpdateBounds(true)
                return
            }
        }

        val maxWidth = widthChars - (if (needsScrollbar) 1 else 0)
        if (maxWidth < 1) {
            return
        }

//        val minSize = text.withoutSGRCodes().split(' ').maxOfOrNull { it.length } ?: 1
//        if (minSize > maxWidth) {
//            return
//        }

        val parts = text.partitionSRG().toMutableList()
        var lineWidth = 0

        if (buffer.isNotBlank() && parts.isNotEmpty()) {
            buffer += "\n"
        }

        while (parts.isNotEmpty()) {
            val part = parts.removeFirst()
            var current = part.text
            var remaining = ""

            while (lineWidth + current.length > maxWidth) {
                val words = current.split(' ').toMutableList()
                remaining = (words.removeLast() + ' ' + remaining).removeSuffix(" ")
                current = words.joinToString(" ")
            }
            buffer += part.fmt + current
            lineWidth += current.length

            if (remaining.withoutSGRCodes() == text.withoutSGRCodes().strip() && (maxWidth - lineWidth) > 0) {
                // Unable to print anything, truncate
                val partsRemaining = remaining.split(' ').toMutableList()
                val firstWord = (if (addToReal) "" else " ") + partsRemaining.removeFirst().strip()
                buffer += firstWord.substring(0, maxWidth - lineWidth)
//                if (maxWidth > 10) {
//                    buffer = buffer.substring(0, buffer.length - 3) + "..."
//                }
                lineWidth = maxWidth
                remaining = partsRemaining.joinToString(" ")
            }

            if (remaining.isNotBlank()) {
                addLine(" ${part.fmt}${remaining.strip()}", false, causedByUpdate)
                addLine(parts.joinToString { "${it.fmt}${it.text}".strip() }, false, causedByUpdate)
                break
            }
        }
    }

    override fun onScrolled(x: Int, y: Int) {
        println("Scroll: $x, $y")
    }

    private fun realOnUpdateBounds(causedByUpdate: Boolean) {
        val tmp = realText
        realText = ""
        buffer = ""
        tmp.split('\n').forEach {
            addLine(it, true, causedByUpdate)
        }
    }
    override fun onUpdateBounds() {
        super.onUpdateBounds()
        realOnUpdateBounds(false)
    }
}
