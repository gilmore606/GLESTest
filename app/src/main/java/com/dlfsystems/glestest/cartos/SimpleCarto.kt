package com.dlfsystems.glestest.cartos

import com.dlfsystems.glestest.Level
import com.dlfsystems.glestest.util.Rect
import com.dlfsystems.glestest.util.Tile.*

object SimpleCarto : Carto() {

    override fun carveLevel() {
        carveRoom(Rect(5, 5, 11, 9), 0)
        carveRoom(Rect(8, 12, 17, 18), 1)
        carveRoom(Rect(11, 9, 11, 12), 2)
        carveRoom(Rect(13, 16, 13, 16), 3, WALL)
    }

}
