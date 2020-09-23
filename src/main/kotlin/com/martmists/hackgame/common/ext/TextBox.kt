package com.martmists.hackgame.common.ext

import com.googlecode.lanterna.gui2.TextBox

fun TextBox.setLine(index: Int, line: String) {
    val parts = text.split("\n").toMutableList()
    parts[index] = line
    text = parts.joinToString("\n")
}
