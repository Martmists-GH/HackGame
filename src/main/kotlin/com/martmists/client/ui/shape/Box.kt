package com.martmists.client.ui.shape

data class Box(val x: Int, val y: Int, val width: Int, val height: Int) {
    operator fun contains(other: Position): Boolean {
        return other.x in x.toFloat().rangeTo(x + width.toFloat()) && other.y in y.toFloat().rangeTo(y + height.toFloat())
    }

    operator fun contains(other: Box): Boolean {
        return Position(other.x, other.y) in this && Position(other.x + other.width, other.y + other.height) in this
    }
}
