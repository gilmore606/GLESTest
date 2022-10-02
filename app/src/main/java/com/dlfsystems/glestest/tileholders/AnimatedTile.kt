package com.dlfsystems.glestest.tileholders

import com.dlfsystems.glestest.Level
import com.dlfsystems.glestest.render.TileSet
import java.nio.FloatBuffer

class AnimatedTile(
    set: TileSet
): TileHolder(set) {

    override fun getTextureIndex(level: Level?, x: Int, y: Int): Int {
        TODO("Not yet implemented")
    }

}
