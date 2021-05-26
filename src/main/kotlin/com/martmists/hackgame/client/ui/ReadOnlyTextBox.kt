package com.martmists.hackgame.client.ui

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke

class ReadOnlyTextBox(preferredSize: TerminalSize, initialContent: String, style: Style) : TextBox(preferredSize, initialContent, style) {
    init {
        this.isReadOnly = true
    }

    constructor() : this(TerminalSize(100, 100), "", Style.MULTI_LINE)
    constructor(size: TerminalSize) : this(size, "", Style.MULTI_LINE)
    constructor(size: TerminalSize, content: String) : this(size, content, Style.MULTI_LINE)

    override fun onBeforeDrawing() {
        super.onBeforeDrawing()
    }

    override fun isFocusable() = false
    override fun handleKeyStroke(keyStroke: KeyStroke) = Interactable.Result.UNHANDLED

    override fun addLine(line: String): TextBox {
        for (seg in line.split("\n")) {
            super.addLine(seg)
            scrollDown()
        }
        return this
    }

    fun scrollDown() {
        setCaretPosition(Int.MAX_VALUE, 0)
    }

    fun scrollUp() {
        setCaretPosition(0, 0)
    }
}