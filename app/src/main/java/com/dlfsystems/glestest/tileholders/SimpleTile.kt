package com.dlfsystems.glestest.tileholders

import com.dlfsystems.glestest.render.TileSet
import java.nio.FloatBuffer

class SimpleTile(
    set: TileSet,
    val tx: Int,
    val ty: Int
    ): TileHolder(set) {

    override fun getTexCoords(outBuffer: FloatBuffer) {
        outBuffer.clear()
        outBuffer.put(tx * set.tileRowStride)
        outBuffer.put(ty * set.tileColumnStride)
        outBuffer.put((tx + 1) * set.tileRowStride)
        outBuffer.put((ty + 1) * set.tileColumnStride)
    }

}
