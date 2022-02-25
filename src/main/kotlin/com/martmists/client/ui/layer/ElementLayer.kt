package com.martmists.client.ui.layer

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.element.Element
import com.martmists.client.ui.shape.Position
import org.lwjgl.util.yoga.Yoga.*

abstract class ElementLayer : RenderLayer {
    private val layout = YGNodeNew().also {
        YGNodeStyleSetFlex(it, 1.0f);
    }
    private val elements = mutableSetOf<Element>()
    abstract var selected: Element

    fun addElement(element: Element) {
        YGNodeInsertChild(layout, element.node, elements.size)
        elements.add(element)
    }

    fun removeElement(element: Element) {
        elements.remove(element)
        YGNodeRemoveChild(layout, element.node)
    }

    override fun getElementsAt(x: Int, y: Int): List<Element> {
        val pos = Position(x, y)
        return elements.filter { pos in it.bounds }
    }

    override fun render(context: RenderContext) {
        elements.forEach { it.render(context) }
    }

    override fun onResize(width: Int, height: Int) {
        YGNodeStyleSetWidth(layout, width.toFloat())
        YGNodeStyleSetHeight(layout, height.toFloat())

        YGNodeCalculateLayout(layout, width.toFloat(), height.toFloat(), YGFlexDirectionColumn)
        elements.forEach {
            it.bounds = it.fromNodeSize()
        }
    }

    override fun onClicked(x: Int, y: Int) {
        getElementsAt(x, y).firstOrNull()?.let {
            selected = it
            selected.onClicked(x - selected.bounds.x, y - selected.bounds.y)
        }
    }

    override fun onKeyPressed(key: Int) {
        selected.onKeyPressed(key)
    }

    override fun onCharPressed(char: Char) {
        selected.onCharPressed(char)
    }

    override fun onScrolled(x: Int, y: Int) {
        selected.onScrolled(x, y)
    }
}
