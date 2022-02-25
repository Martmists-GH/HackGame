package com.martmists.common.ext

fun Float.notNan() : Float {
    return if (this.isNaN()) 1f else this
}
