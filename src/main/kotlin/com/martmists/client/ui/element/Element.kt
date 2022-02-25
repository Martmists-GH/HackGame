package com.martmists.client.ui.element

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.shape.Box
import com.martmists.common.ext.notNan
import com.martmists.common.utilities.Loggable
import io.github.humbleui.skija.Paint
import org.lwjgl.util.yoga.YGNode
import org.lwjgl.util.yoga.Yoga.*
import kotlin.math.roundToInt
import kotlin.error as ktError


abstract class Element : Loggable {
    val node = YGNodeNew()
    open var bounds: Box = fromNodeSize()
        set(value) {
            field = value
            onUpdateBounds()
        }

    fun fromNodeSize() : Box {
        val layout = YGNode.create(node).layout()
        val l = layout.positions(YGEdgeLeft)
        val t = layout.positions(YGEdgeTop)
        val w = layout.dimensions(YGDimensionWidth).notNan()
        val h = layout.dimensions(YGDimensionHeight).notNan()
        return Box(l.roundToInt(), t.roundToInt(), w.roundToInt(), h.roundToInt())
    }

    open fun onUpdateBounds() {

    }
    open fun onCharPressed(char: Char) {

    }
    open fun onKeyPressed(key: Int) {

    }
    open fun onClicked(x: Int, y: Int) {

    }
    open fun onScrolled(x: Int, y: Int) {

    }

    abstract fun render(ctx: RenderContext)

    protected fun getPaint(ctx: RenderContext, fmt: String = "") : Paint {
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
}
