package com.dlfsystems.glestest

import kotlin.math.pow
import kotlin.math.sqrt

class XY(val x: Int, val y: Int) {
    operator fun plus(b: XY): XY {
        return XY(x + b.x, y + b.y)
    }
    operator fun minus(b: XY): XY {
        return XY(x - b.x, y - b.y)
    }
    fun distanceTo(b: XY): Float {
        val x0 = x.toFloat()
        val x1 = b.x.toFloat()
        val y0 = y.toFloat()
        val y1 = b.y.toFloat()
        return sqrt((x1-x0).pow(2) + (y1-y0).pow(2))
    }
}
