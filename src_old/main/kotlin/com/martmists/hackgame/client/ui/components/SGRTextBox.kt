package com.martmists.client.ui.components

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.utils.Box
import com.martmists.client.ui.utils.Position
import com.martmists.client.ui.utils.Size
import com.martmists.common.ext.partitionSRG
import com.martmists.common.ext.withoutSGRCodes
import io.github.humbleui.skija.Paint
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.error as ktError

open class SGRTextBox(
    private val xPercent: Float,
    private val yPercent: Float,
    private val widthPercent: Float,
    private val heightPercent: Float,
) : TextBoxElement() {
    private var title = "Window Title"

    override val desiredBox: Box
        get() = Box(xPercent, yPercent, widthPercent, heightPercent)
    open val border = "╔═╗║╚╝"

    private var realText = ""
    private var buffer = ""

    private val widthChars: Int
        get() = floor(box.width / charSize).roundToInt()
    private val heightChars: Int
        get() = floor(box.height / fontSize).roundToInt()

    private fun getPaint(ctx: RenderContext, fmt: String = "") : Paint {
        return Paint().apply {
            if (fmt == "") {
                color = 0xFF000000.toInt() + ctx.theme.DEFAULT
                return@apply
            }

            val stripped = fmt.substring(2 until fmt.length - 1)
            val args = stripped.split(';')
            val arg = args[0].toInt()
            when (arg) {
                0 -> {
                    color = 0xFF000000.toInt() + ctx.theme.DEFAULT
                }

                in 30 until 38 -> {
                    when (args.size) {
                        1 -> {
                            // Regular
                            color = 0xFF000000.toInt() + when (arg) {
                                30 -> ctx.theme.BLACK
                                31 -> ctx.theme.RED
                                32 -> ctx.theme.GREEN
                                33 -> ctx.theme.YELLOW
                                34 -> ctx.theme.BLUE
                                35 -> ctx.theme.MAGENTA
                                36 -> ctx.theme.CYAN
                                37 -> ctx.theme.WHITE
                                else -> ktError("Should not happen!")
                            }
                        }
                        2 -> {
                            // Bold
                            color = 0xFF000000.toInt() + when (arg) {
                                30 -> ctx.theme.BRIGHT_BLACK
                                31 -> ctx.theme.BRIGHT_RED
                                32 -> ctx.theme.BRIGHT_GREEN
                                33 -> ctx.theme.BRIGHT_YELLOW
                                34 -> ctx.theme.BRIGHT_BLUE
                                35 -> ctx.theme.BRIGHT_MAGENTA
                                36 -> ctx.theme.BRIGHT_CYAN
                                37 -> ctx.theme.BRIGHT_WHITE
                                else -> ktError("Should not happen!")
                            }
                        }
                        else -> {
                            return@apply
                        }
                    }
                }

                38 -> {
                    // 256/24bit color

                    when (args[1].toInt()) {
                        2 -> {
                            // RGB
                            color = 0xFF000000.toInt() + (args[2].toInt().coerceAtMost(255) shl 16) + (args[3].toInt().coerceAtMost(255) shl 8) + args[4].toInt().coerceAtMost(255)
                        }

                        5 -> {
                            // 256
                            val red: Int
                            val green: Int
                            val blue: Int

                            var i = args[2].toInt()

                            when (i) {
                                7 -> {
                                    red = 192;
                                    green = 192;
                                    blue = 192;
                                }
                                in 0 until 16 -> {
                                    if (i == 8) {
                                        i = 7
                                    }
                                    red = if (i % 2 == 0) 0 else if (i > 7) 255 else 128
                                    green = if ((i/2) % 2 == 0) 0 else if (i > 7) 255 else 128
                                    blue = if ((i/4) % 2 == 0) 0 else if (i > 7) 255 else 128
                                }
                                in 16 until 232 -> {
                                    val j = i - 16;
                                    val r = j / 36
                                    val g = (j / 6) % 6
                                    val b = j % 6;
                                    red = if (r == 0) 0 else r * 40 + 55;
                                    green = if (g == 0) 0 else g * 40 + 55;
                                    blue = if (b == 0) 0 else b * 40 + 55;
                                }

                                in 232 until 256 -> {
                                    val x = 10 * (i - 232) + 8
                                    red = x
                                    green = x
                                    blue = x
                                }
                                else -> return@apply
                            }

                            color = 0xFF000000.toInt() + (red shl 16) + (green shl 8) + blue
                        }
                    }
                }

                else -> ktError(arg)
            }
        }
    }

    fun addLine(text: String, addToReal: Boolean = true) {
        if (addToReal) {
            if (realText == "") {
                realText = text
            } else {
                realText += "\n$text"
            }
        }

        val minSize = (text.withoutSGRCodes().split(' ').maxOfOrNull { it.length } ?: 1) * charSize
        if (minSize > box.width) {
            return
        }

        buffer += "\n"

        val parts = text.partitionSRG().toMutableList()
        var lineWidth = 4  // Border

        while (parts.isNotEmpty()) {
            val part = parts.removeFirst()
            var current = part.text
            var remaining = ""

            while (lineWidth + current.length > widthChars) {
                val words = current.split(' ').toMutableList()
                remaining = (words.removeLast() + ' ' + remaining).removeSuffix(" ")
                current = words.joinToString(" ")
            }
            buffer += part.fmt + current
            lineWidth += current.length

            if (remaining.withoutSGRCodes() == text.withoutSGRCodes().strip()) {
                // Unable to print anything
                break
            }

            if (remaining != "") {
                addLine(" ${part.fmt}$remaining", false)
                addLine(parts.joinToString { "${it.fmt}${it.text}" }, false)
                break
            }
        }
    }

    override fun render(context: RenderContext) {
        // TODO: Scrolling support?

        val onScreen = buffer.split('\n').let {
            it.subList((it.size-heightChars-2).coerceAtLeast(0), it.size).joinToString("\n")
        }

        // Draw border
        val borderPaint = getPaint(context)

        var name = title
        if (widthChars < title.length + 5) {
            name = ""
        }
        var filler = ""
        if (widthChars-4-name.length >= 0) {
            filler = border[1].toString().repeat(widthChars - 4 - name.length)
        }
        drawString(context, "${border[0]}${border[1]}$name${border[1]}$filler${border[2]}", Position(0f, 1f), borderPaint)
        (2 until heightChars).forEach { y ->
            drawString(context, "${border[3]}${" ".repeat(widthChars-2)}${border[3]}", Position(0f, y.toFloat()), borderPaint)
        }
        drawString(context, "${border[4]}${border[1].toString().repeat(widthChars-2)}${border[5]}", Position(0f, (heightChars).toFloat()), borderPaint)

        onScreen.split('\n').forEachIndexed { index, line ->
            var x = 2
            for (part in line.partitionSRG()) {
                getPaint(context, part.fmt).use {
                    drawString(context, part.text, Position(x.toFloat(), index + 1f), it)
                }
                x += part.text.length
            }
        }
    }

    override fun onResize(size: Size) {
        super.onResize(size)

        val oldText = realText
        buffer = ""
        realText = ""
        for (line in oldText.split('\n')) {
            addLine(line)
        }
    }
}
