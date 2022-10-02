package com.dlfsystems.glestest.render

import android.graphics.BitmapFactory
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLUtils
import com.dlfsystems.glestest.util.Tile
import com.dlfsystems.glestest.tileholders.TileHolder
import java.nio.FloatBuffer
import kotlin.collections.HashMap

class TileSet(
    resourceId: Int,
    val tilesPerRow: Int,
    val tilesPerColumn: Int,
    context: Context
) {
    var tileHolders: HashMap<Tile, TileHolder> = HashMap()
    var tileRowStride = 0.0f
    var tileColumnStride = 0.0f

    var textureHandle = 0

    init {
        tileRowStride = (1.0f / tilesPerRow)
        tileColumnStride = (1.0f / tilesPerColumn)

        val texHandle = intArrayOf(0, 0)
        GLES20.glGenTextures(1, texHandle, 0)
        val options = BitmapFactory.Options().apply { inScaled = false }
        val bitmap =
            BitmapFactory.decodeResource(context.resources, resourceId, options)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texHandle[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_NEAREST
        )
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        textureHandle = texHandle[0]
    }

    fun setTile(tile: Tile, holder: TileHolder) {
        tileHolders[tile] = holder
    }

    fun getTexCoordsForTile(tile: Tile, outBuffer: FloatBuffer) {
        tileHolders[tile]?.getTexCoords(outBuffer)
    }
}
