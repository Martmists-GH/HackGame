package com.martmists.client.ui.shape

data class Size(val width: Float, val height: Float) {
    constructor(width: Int, height: Int) : this(width.toFloat(), height.toFloat())

    override fun toString() = "[${width.toInt()}x${height.toInt()}]"
}
