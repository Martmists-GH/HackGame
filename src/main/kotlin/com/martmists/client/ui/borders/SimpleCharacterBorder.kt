package com.martmists.client.ui.borders

class SimpleCharacterBorder(private val chars: String = "═║╔╗╚╝") : Border() {
    override fun getBorder(width: Int, height: Int) : String {
        return (0 until height).joinToString("\n") { row ->
            (0 until width).map { col ->
                when (row) {
                    0 -> {
                        when (col) {
                            0 -> chars[2]
                            width - 1 -> chars[3]
                            else -> chars[0]
                        }
                    }
                    height - 1 -> {
                        when (col) {
                            0 -> chars[4]
                            width - 1 -> chars[5]
                            else -> chars[0]
                        }
                    }
                    else -> {
                        when (col) {
                            0, width - 1 -> chars[1]
                            else -> " "
                        }
                    }
                }
            }.joinToString("")
        }
    }

    override fun getBorder(width: Int, height: Int, title: String): String {
        var text = getBorder(width, height)
        if (width - 4 >= title.length) {
            text = text.replace(text.substring(0, 2+title.length), text.substring(0, 2) + title)
        }
        return text
    }
}
