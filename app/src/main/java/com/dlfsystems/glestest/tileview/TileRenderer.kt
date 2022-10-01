package com.dlfsystems.glestest.tileview

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.dlfsystems.glestest.Level
import com.dlfsystems.glestest.R
import com.dlfsystems.glestest.Tile.*
import com.dlfsystems.glestest.XY
import com.dlfsystems.glestest.opengl.DrawList
import com.dlfsystems.glestest.opengl.TileSet
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

    var center = XY(5,5)

    private var width = 0
    private var height = 0

    private lateinit var dungeonTiles: TileSet
    private lateinit var dungeonDrawList: DrawList

    private var level: Level? = null
    private var stride: Double = 0.01
    private var pixelStride: Double = 0.01

    fun observeLevel(newLevel: Level) {
        level = newLevel
    }

    // Convert an XY pixel touch event to an absolute tile XY on the level.
    fun touchToTileXY(touchX: Float, touchY: Float): XY {
        val x = (touchX - viewLocation.x) + pixelStride / 2 - width / 2
        val y = (touchY - viewLocation.y) - height / 2
        val xSign = x.sign.toInt()
        val ySign = y.sign.toInt()
        Timber.d("$x $y stride $pixelStride")
        return XY(
            abs(x / pixelStride).toInt() * xSign + (if (xSign == -1) -1 else 0) + center.x,
            abs(y / pixelStride).toInt() * ySign + (if (ySign == -1) -1 else 0) + center.y
        )
    }


    override fun onSurfaceCreated(p0: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        allocateResources()
    }

    override fun onSurfaceChanged(p0: GL10?, newWidth: Int, newHeight: Int) {
        GLES20.glViewport(0, 0, newWidth, newHeight)
        dungeonDrawList.aspectRatio = newWidth.toDouble() / newHeight.toDouble()
        width = newWidth
        height = newHeight
        updateStride()
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        dungeonDrawList.clear()

        level?.also { level ->
            for (tx in 0 until level.width) {
                for (ty in 0 until level.height) {
                    val x0 = (tx - center.x).toDouble() * stride - (stride * 0.5)
                    val y0 = (ty - center.y).toDouble() * stride - (stride * 0.5)
                    // TODO: optimize: only add onscreen quads
                    dungeonDrawList.addQuad(x0, y0, x0 + stride, y0 + stride, level.tiles[tx][ty])
                }
            }
        }

        dungeonDrawList.draw()
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
    }

}
