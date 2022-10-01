package com.dlfsystems.glestest.cartos

import com.dlfsystems.glestest.Level
import com.dlfsystems.glestest.Tile.*

object SimpleCarto : Carto() {

    override fun makeLevel(): Level {

        val level = Level(80,80)

        level.makeRoom(5, 5, 11, 9)
        level.makeRoom(8, 12, 17, 18)
        level.makeRoom(11, 9, 11, 12)
        return level
    }

}
