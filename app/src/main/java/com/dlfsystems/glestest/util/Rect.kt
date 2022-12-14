package com.dlfsystems.glestest.util

class Rect(var x0: Int, var y0: Int, var x1: Int, var y1: Int) {

    fun isTouching(other: Rect): Boolean {
        if (x0 > other.x1 + 1) return false
        if (x1 < other.x0 - 1) return false
        if (y0 > other.y1 + 1) return false
        if (y1 < other.y0 - 1) return false
        return true
    }

    fun contains(xy: XY): Boolean = (xy.x >= x0 && xy.y >= y0 && xy.x <= x1 && xy.y <= y1)

}
