package com.martmists.client.ui.element

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.shape.Position
import com.martmists.common.utilities.TextColor
import org.lwjgl.glfw.GLFW.*

class InputElement(private val onSubmit: (String) -> Unit) : TextElement("", false) {
    private var text = ""
    private val renderText: String
        get() = "$text "
    private var cursorPos = 0
    private var visibleStart = 0

    private var counter = 0
    private val counterLimit = 60

    override fun render(ctx: RenderContext) {
        val toRender = renderText.substring(visibleStart, (visibleStart + widthChars).coerceAtMost(renderText.length))
        drawString(ctx, toRender, Position(0, 0), getPaint(ctx, TextColor.RESET.toString()))

        if (counter++ < counterLimit / 2) {
            drawString(ctx, "_", Position((cursorPos - visibleStart).coerceAtMost(widthChars-1), 0), getPaint(ctx, TextColor.RESET.toString()))
        }

        counter %= counterLimit
    }

    override fun onKeyPressed(key: Int) {
        when (key) {
            GLFW_KEY_ENTER -> {
                if (text.isNotBlank()) {
                    onSubmit(text)
                }
                text = ""
                cursorPos = 0
            }
            GLFW_KEY_BACKSPACE -> {
                text = text.substring(0, (cursorPos-1).coerceAtLeast(0)) + text.substring(cursorPos)
                cursorPos--
            }
            GLFW_KEY_DELETE -> {
                text = text.substring(0, cursorPos) + text.substring(cursorPos + 1)
            }

            GLFW_KEY_LEFT -> {
                cursorPos--
            }
            GLFW_KEY_RIGHT -> {
                cursorPos++
            }
            GLFW_KEY_HOME -> {
                cursorPos = 0
            }
            GLFW_KEY_END -> {
                cursorPos = text.length
            }
        }

        updateRange()
    }

    override fun onCharPressed(char: Char) {
        if (cursorPos == text.length) {
            text += char
        } else {
            text = text.substring(0, cursorPos) + char + text.substring(cursorPos)
        }
        cursorPos++

        updateRange()
    }

    private fun updateRange() {
        counter = 0
        if (cursorPos > text.length) {
            cursorPos = text.length
        } else if (cursorPos < 0) {
            cursorPos = 0
        }

        if (visibleStart + widthChars > renderText.length) {
            visibleStart = 0
        }

        if (renderText.length > widthChars) {
            if (cursorPos < visibleStart) {
                visibleStart = cursorPos
            } else if (cursorPos > visibleStart + widthChars - 1) {
                visibleStart = cursorPos - widthChars
                if (cursorPos == text.length) {
                    visibleStart++
                }
            }
        } else {
            visibleStart = 0
        }
    }
}
