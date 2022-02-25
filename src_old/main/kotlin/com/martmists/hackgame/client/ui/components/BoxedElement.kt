package com.martmists.client.ui.components

import com.martmists.client.ui.RenderContext
import com.martmists.client.ui.events.ResizeEvent
import com.martmists.client.ui.utils.Box
import com.martmists.client.ui.utils.Position
import com.martmists.client.ui.utils.Size

abstract class BoxedElement {
    abstract val desiredBox: Box

    init {
        ResizeEvent.EVENT.addListener { size -> this.onResize(size) }
    }
    var box = Box(0f, 0f, 0f, 0f)

    abstract fun render(context: RenderContext)

    open fun onResize(size: Size) {
        box = Box(
            size.width * desiredBox.x,
            size.height - (size.height * desiredBox.y),
            size.width * desiredBox.width,
            size.height * desiredBox.height)
        println("moved to $box")
    }

    fun absolute(position: Position) : Position {
        return Position(
            box.x + position.x,
            box.y - position.y
        )
    }
}
