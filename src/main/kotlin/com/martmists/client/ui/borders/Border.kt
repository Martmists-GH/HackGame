package com.martmists.client.ui.borders

abstract class Border {
    abstract fun getBorder(width: Int, height: Int) : String
    abstract fun getBorder(width: Int, height: Int, title: String) : String
}
