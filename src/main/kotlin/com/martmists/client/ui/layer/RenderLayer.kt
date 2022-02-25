package com.martmists.client.ui.layer

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.element.Element

interface RenderLayer {
    fun render(context: RenderContext)
    fun getElementsAt(x: Int, y: Int): List<Element>

    fun onResize(width: Int, height: Int) {

    }
    fun onCharPressed(char: Char) {

    }
    fun onKeyPressed(key: Int) {

    }
    fun onClicked(x: Int, y: Int) {

    }
    fun onScrolled(x: Int, y: Int) {

    }
}
