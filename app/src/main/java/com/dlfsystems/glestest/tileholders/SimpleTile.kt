package com.dlfsystems.glestest.tileholders

import com.dlfsystems.glestest.Level
import com.dlfsystems.glestest.render.TileSet

class SimpleTile(
    set: TileSet,
    val tx: Int,
    val ty: Int
    ): TileHolder(set) {

    override fun getTextureIndex(level: Level?, x: Int, y: Int): Int {
        return ty * set.tilesPerRow + tx
    }

}
