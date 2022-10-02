package com.dlfsystems.glestest.tileholders

import com.dlfsystems.glestest.render.TileSet
import com.dlfsystems.glestest.util.XY
import java.nio.FloatBuffer

class VariantsTile(
    set: TileSet
): TileHolder(set) {

    private val variants = ArrayList<Triple<Float, Int, Int>>()

    fun add(frequency: Float, tx: Int, ty: Int) {
        variants.add(Triple(frequency, tx, ty))
    }

    override fun getTexCoords(outBuffer: FloatBuffer) {

    }

}
