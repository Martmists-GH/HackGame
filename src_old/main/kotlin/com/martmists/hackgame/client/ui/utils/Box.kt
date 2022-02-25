package com.martmists.client.ui.utils

data class Box(val position: Position, val size: Size) {
    constructor(x: Float, y: Float, width: Float, height: Float) : this(Position(x, y), Size(width, height))

    fun offset(x: Float, y: Float) = Box(position.offset(x, y), size)
    fun offset(x: Int, y: Int) = Box(position.offset(x, y), size)

    val x: Float
        get() = position.x
    val y: Float
        get() = position.y
    val width: Float
        get() = size.width
    val height: Float
        get() = size.height

    override fun toString() = "$position $size"
}
