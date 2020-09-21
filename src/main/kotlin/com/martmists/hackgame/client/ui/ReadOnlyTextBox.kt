package com.martmists.hackgame.client.ui

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke

class ReadOnlyTextBox(preferredSize: TerminalSize, initialContent: String, style: Style) : TextBox(preferredSize, initialContent, style) {
    init {
        this.isReadOnly = true
    }

    constructor() : this(TerminalSize(10, 1), "", Style.SINGLE_LINE)
    constructor(size: TerminalSize) : this(size, "", Style.SINGLE_LINE)
    constructor(size: TerminalSize, content: String) : this(size, content, Style.SINGLE_LINE)

    override fun isFocusable() = true
    override fun handleKeyStroke(keyStroke: KeyStroke) = Interactable.Result.UNHANDLED

    fun scrollDown() {
        caretPosition.withColumn(caretPosition.column + 1)
    }

    fun scrollUp() {
        caretPosition.withColumn((caretPosition.column - 1).coerceAtLeast(0))
    }
}