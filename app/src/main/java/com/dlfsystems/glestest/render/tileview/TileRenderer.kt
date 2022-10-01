package com.dlfsystems.glestest.render.tileview

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.dlfsystems.glestest.Level
import com.dlfsystems.glestest.R
import com.dlfsystems.glestest.Tile
import com.dlfsystems.glestest.Tile.*
import com.dlfsystems.glestest.XY
import com.dlfsystems.glestest.render.DrawList
import com.dlfsystems.glestest.render.TileSet
import com.dlfsystems.glestest.shaders.tileFragShader
import com.dlfsystems.glestest.shaders.tileVertShader
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.sign

class TileRenderer(val context: Context) : GLSurfaceView.Renderer {

    var zoom = 2.0
        set(value) {
            field = value
            updateStride()
        }

    var viewLocation = XY(0, 0)

    private var width = 0
    private var height = 0

    private lateinit var dungeonTiles: TileSet
    private lateinit var dungeonDrawList: DrawList
    private lateinit var mobTiles: TileSet
    private lateinit var mobDrawList: DrawList

    private var level: Level? = null
    private var stride: Double = 0.01
    private var pixelStride: Double = 0.01

    private val pov: XY
        get() = level?.pov ?: XY(0,0)

    fun observeLevel(newLevel: Level) {
        level = newLevel
    }

    fun moveCenter(newX: Int, newY: Int) {
        level?.pov = XY(newX, newY)
    }

    // Convert an XY pixel touch event to an absolute tile XY on the level.
    fun touchToTileXY(touchX: Float, touchY: Float): XY {
        val x = (touchX - viewLocation.x) + pixelStride / 2 - width / 2
        val y = (touchY - viewLocation.y) - height / 2
        val xSign = x.sign.toInt()
        val ySign = y.sign.toInt()
        Timber.d("$x $y stride $pixelStride")
        return XY(
            abs(x / pixelStride).toInt() * xSign + (if (xSign == -1) -1 else 0) + pov.x,
            abs(y / pixelStride).toInt() * ySign + (if (ySign == -1) -1 else 0) + pov.y
        )
    }


    override fun onSurfaceCreated(p0: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        allocateResources()
    }

    override fun onSurfaceChanged(p0: GL10?, newWidth: Int, newHeight: Int) {
        GLES20.glViewport(0, 0, newWidth, newHeight)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GLES20.GL_BLEND)
        val aspectRatio = newWidth.toDouble() / newHeight.toDouble()
        dungeonDrawList.aspectRatio = aspectRatio
        mobDrawList.aspectRatio = aspectRatio
        width = newWidth
        height = newHeight
        updateStride()
    }

    override fun onDrawFrame(p0: GL10?) {
        fun DrawList.addTileQuad(col: Int, row: Int, tile: Tile, visibility: Float) {
            val x0 = col.toDouble() * stride - (stride * 0.5)
            val y0 = row.toDouble() * stride - (stride * 0.5)
            addQuad(x0, y0, x0 + stride, y0 + stride, tile, visibility)
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        dungeonDrawList.clear()
        mobDrawList.clear()

        level?.also { level ->
            for (tx in 0 until level.width) {
                for (ty in 0 until level.height) {
                    // TODO: optimize: only add onscreen quads
                    dungeonDrawList.addTileQuad(
                        tx - pov.x,
                        ty - pov.y,
                        level.tiles[tx][ty],
                        level.visibility[tx][ty]
                    )
                }
            }
        }

        dungeonDrawList.draw()

        mobDrawList.addTileQuad(0, 0, PLAYER, 1f)
        mobDrawList.draw()
    }

    // Recalculate the size in glcoords of one tile
    private fun updateStride() {
        stride = 1.0 / (height.coerceAtLeast(400).toDouble() * 0.01f) * zoom
        pixelStride = height / (2.0 / stride)
    }

    // Make all the buffers etc we need on surface creation.
    private fun allocateResources() {
        dungeonTiles = TileSet(R.drawable.tiles_dungeon, 10, 10, context).apply {
            setTile(FLOOR, 6, 0)
            setTile(WALL, 1, 0)
            setTile(CLOSED_DOOR, 6, 3)
            setTile(OPEN_DOOR, 6, 4)
        }

        dungeonDrawList = DrawList(tileVertShader(), tileFragShader(), dungeonTiles)

        mobTiles = TileSet(R.drawable.tiles_mob, 7, 4, context).apply {
            setTile(PLAYER, 2, 2)
        }

        mobDrawList = DrawList(tileVertShader(), tileFragShader(), mobTiles)
    }

}
