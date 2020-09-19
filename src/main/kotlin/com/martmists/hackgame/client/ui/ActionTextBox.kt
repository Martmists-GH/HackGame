package com.martmists.hackgame.client.ui

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType

class ActionTextBox(preferredSize: TerminalSize, initialContent: String, style: Style) : TextBox(preferredSize, initialContent, style) {
    val history = mutableListOf<String>()
    var historyIndex = -1
    var callback: ((String) -> Unit)? = null

    constructor() : this(TerminalSize(10, 1), "", Style.SINGLE_LINE)
    constructor(size: TerminalSize) : this(size, "", Style.SINGLE_LINE)
    constructor(size: TerminalSize, content: String) : this(size, content, Style.SINGLE_LINE)

    override fun handleKeyStroke(keyStroke: KeyStroke): Interactable.Result {
        if (keyStroke.keyType == KeyType.Enter) {
            if (this.text.isBlank()) {
                return Interactable.Result.HANDLED
            }
            history.add(0, this.text)
            historyIndex = -1
            callback?.invoke(this.text)
            this.text = ""
            this.setCaretPosition(this.text.length)
            return Interactable.Result.HANDLED
        }

        if (keyStroke.keyType == KeyType.ArrowUp) {
            historyIndex += 1
            this.text = history.getOrNull(historyIndex) ?: this.text
            if (historyIndex >= history.size) {
                historyIndex = history.size  // Prevent it from going too far
            }
            this.setCaretPosition(this.text.length)
            return Interactable.Result.HANDLED
        }

        if (keyStroke.keyType == KeyType.ArrowDown) {
            historyIndex -= 1
            this.text = history.getOrNull(historyIndex) ?: this.text
            if (historyIndex <= -1) {
                historyIndex = -1  // Prevent it from going too far
                this.text = ""
            }
            this.setCaretPosition(this.text.length)
            return Interactable.Result.HANDLED
        }

        if (keyStroke.keyType == KeyType.ArrowLeft) {
            setCaretPosition((caretPosition.column - 1).coerceAtLeast(0))
            return Interactable.Result.HANDLED
        }

        if (keyStroke.keyType == KeyType.ArrowRight) {
            setCaretPosition((caretPosition.column + 1).coerceAtMost(this.text.length))
            return Interactable.Result.HANDLED
        }

        return super.handleKeyStroke(keyStroke)
    }
}
