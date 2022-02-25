package com.martmists.client.ui.utils

data class Position(val x: Float, val y: Float) {
    fun offset(x: Float, y: Float) : Position = Position(this.x + x, this.y + y)
    fun offset(x: Int, y: Int) : Position = Position(this.x + x, this.y + y)

    override fun toString() = "($x, $y)"
}
