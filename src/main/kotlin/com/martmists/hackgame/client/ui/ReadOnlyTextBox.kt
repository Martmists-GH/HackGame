package com.martmists.hackgame.client.ui

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke

class ReadOnlyTextBox(preferredSize: TerminalSize, initialContent: String, style: Style) : TextBox(preferredSize, "", style) {
    var fullText: String

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

        for (line in fullText.split("\n")) {
            tryAddLine(line)
        }

        val lineCount = newText.count { it == '\n' } + 1
        if (lineCount > size.rows) {
            newText = newText.split("\n").subList((lineCount - size.rows - 2).coerceAtLeast(0), lineCount).joinToString("\n")
        }
        text = newText
        setCaretPosition(lineCount, 0)
    }

    override fun isFocusable() = false
    override fun handleKeyStroke(keyStroke: KeyStroke) = Interactable.Result.UNHANDLED

    fun addText(line: String) {
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