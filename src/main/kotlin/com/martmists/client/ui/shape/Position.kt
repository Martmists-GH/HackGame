package com.martmists.client.ui.shape

data class Position(val x: Float, val y: Float) {
    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())
}
