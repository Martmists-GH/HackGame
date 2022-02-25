package com.martmists.client.ui.utils

data class Size(val width: Float, val height: Float) {
    override fun toString() = "[${width.toInt()}x${height.toInt()}]"
}
