package com.martmists.client.ui.element

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.borders.SimpleCharacterBorder
import com.martmists.client.ui.shape.Box
import com.martmists.client.ui.shape.Position
import org.lwjgl.util.yoga.Yoga.YGNodeInsertChild
import kotlin.math.roundToInt

open class Window(private val child: Element) : TextElement("", false) {
    init {
        YGNodeInsertChild(node, child.node, 0)
    }

    var title = "Window"
    open val border = SimpleCharacterBorder()

    override fun onUpdateBounds() {
        child.bounds = Box(
            (bounds.x + charSize).roundToInt(),
            (bounds.y + fontSize).roundToInt(),
            (bounds.width - charSize * 2).roundToInt(),
            (bounds.height - fontSize * 2).roundToInt()
        )
    }

    override fun render(ctx: RenderContext) {
        if (widthChars < 3 || heightChars < 3) return
        val text = border.getBorder(widthChars, heightChars, title)
        val paint = getPaint(ctx)
        text.split('\n').forEachIndexed { index, s ->
            drawString(ctx, s, Position(0, index), paint)
        }
        child.render(ctx)
    }

    override fun addLine(text: String, addToReal: Boolean, causedByUpdate: Boolean) {
        if (child is TextElement) {
            child.addLine(text, addToReal, causedByUpdate)
        }
    }

    override fun onCharPressed(char: Char) {
        child.onCharPressed(char)
    }

    override fun onKeyPressed(key: Int) {
        child.onKeyPressed(key)
    }

    override fun onClicked(x: Int, y: Int) {
        child.onClicked(x, y)
    }

    override fun onScrolled(x: Int, y: Int) {
        child.onScrolled(x, y)
    }
}
