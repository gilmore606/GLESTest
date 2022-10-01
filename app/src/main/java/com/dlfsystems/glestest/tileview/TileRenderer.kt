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
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TileRenderer(val context: Context) : GLSurfaceView.Renderer {

    var zoom = 2.0
    val center = XY(5,5)

    private var width = 0
    private var height = 0

    private lateinit var dungeonTiles: TileSet
    private lateinit var dungeonDrawList: DrawList

    private var level: Level? = null

    fun observeLevel(newLevel: Level) {
        level = newLevel
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
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        dungeonDrawList.clear()

        level?.also { level ->
            val stride = 1.0 / (height.coerceAtLeast(400).toDouble() * 0.01f) * zoom
            for (tx in 0 until level.width) {
                for (ty in 0 until level.height) {
                    val x0 = (tx - center.x).toDouble() * stride - (stride * 0.5)
                    val y0 = (ty - center.y).toDouble() * stride - (stride * 0.5)
                    val x1 = x0 + stride
                    val y1 = y0 + stride
                    // TODO: optimize: only add visible quads
                    dungeonDrawList.addQuad(x0, y0, x1, y1, level.tiles[tx][ty])
                }
            }
        }

        dungeonDrawList.draw()
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
