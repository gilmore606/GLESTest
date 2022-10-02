package com.dlfsystems.glestest.tileholders

import com.dlfsystems.glestest.render.TileSet
import java.nio.FloatBuffer

abstract class TileHolder(
    val set: TileSet
    ) {

    abstract fun getTexCoords(outBuffer: FloatBuffer)

}
