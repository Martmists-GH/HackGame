package com.martmists.hackgame.client.ui

import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphicsWriter
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.gui2.TextGUIGraphics
import com.googlecode.lanterna.input.KeyStroke
import com.martmists.hackgame.common.ext.substringSGRAware
import com.martmists.hackgame.common.ext.textLength
import com.martmists.hackgame.common.ext.withoutSGRCodes
import java.util.*

class ReadOnlyTextBox(preferredSize: TerminalSize, initialContent: String, style: Style) : TextBox(preferredSize, "", style) {
    var fullText: String
    var scrollOffset = 0

    init {
        isReadOnly = true
        fullText = initialContent
    }

    constructor() : this(TerminalSize(100, 100), "", Style.MULTI_LINE)
    constructor(size: TerminalSize) : this(size, "", Style.MULTI_LINE)
    constructor(size: TerminalSize, content: String) : this(size, content, Style.MULTI_LINE)

    override fun onBeforeDrawing() {
        if (size.columns < 5) {
            text = fullText
            return
        }

        var newText = ""

        fun tryAddLine(s: String) {
            if (s.length > size.columns) {
                newText += s.substring(0, size.columns) + "\n"
                tryAddLine(" " + s.substring(size.columns))
            } else {
                newText += s + "\n"
            }
        }

        for (line in fullText.withoutSGRCodes().split("\n")) {
            tryAddLine(line)
        }

        val lineCount = newText.count { it == '\n' } + 1
        if (lineCount > size.rows) {
            val min = (lineCount - size.rows - 2 - scrollOffset).coerceAtLeast(0)
            val split = newText.split("\n")
            newText = split.subList(min, (min + size.rows + 1).coerceAtMost(split.size)).joinToString("\n")
        }

        text = newText
        setCaretPosition(lineCount, 0)
    }

    override fun onAfterDrawing(graphics: TextGUIGraphics) {
        if (size.columns < 5) {
            text = fullText
            return
        }

        var newText = ""

        tailrec fun tryAddLine(s: String) {
            if (s.textLength > size.columns) {
                newText += s.substringSGRAware(0, size.columns) + "\n"
                tryAddLine(" " + s.substringSGRAware(size.columns))
            } else {
                newText += s + "\n"
            }
        }

        for (line in fullText.split("\n")) {
            tryAddLine(line)
        }

        val lineCount = newText.count { it == '\n' } + 1
        if (lineCount > size.rows) {
            val min = (lineCount - size.rows - 2 - scrollOffset).coerceAtLeast(0)
            val split = newText.split("\n")
            newText = split.subList(min, (min + size.rows + 1).coerceAtMost(split.size)).joinToString("\n")
        }

        val writer = TextGraphicsWriter(graphics)
        writer.backgroundColor = TextColor.ANSI.BLACK

        newText.split("\n").forEachIndexed { i, line ->
            writer.cursorPosition = TerminalPosition(0, i)
            writer.clearModifiers()
            writer.putString(line)
        }
    }

    override fun isFocusable() = false
    override fun handleKeyStroke(keyStroke: KeyStroke) = Interactable.Result.UNHANDLED

    fun addText(line: String) {
        if (line.isEmpty()) {
            return
        }

        fullText += line
        if (line.last() != '\n') {
            fullText += '\n'
        }
        addLine("")  // Trigger redraw
    }

    fun clearText() {
        fullText = ""
    }

    fun scrollDown() {
        setCaretPosition(Int.MAX_VALUE, 0)
    }

    fun scrollUp() {
        setCaretPosition(0, 0)
    }
}