package com.dlfsystems.glestest.render.tileview

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.dlfsystems.glestest.Level
import com.dlfsystems.glestest.util.Tile.*
import com.dlfsystems.glestest.util.XY
import com.dlfsystems.glestest.render.DrawList
import com.dlfsystems.glestest.render.TileSet
import com.dlfsystems.glestest.shaders.tileFragShader
import com.dlfsystems.glestest.shaders.tileVertShader
import com.dlfsystems.glestest.tilesets.DungeonTileSet
import com.dlfsystems.glestest.tilesets.MobTileSet
import com.dlfsystems.glestest.tilesets.UITileSet
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TileRenderer(val context: Context) : GLSurfaceView.Renderer {

    var zoom = 2.0
        set(value) {
            field = value
            updateSurfaceParams()
        }

    var androidViewLocation = XY(0, 0)

    private var width = 0
    private var height = 0
    private var aspectRatio = 1.0

    private lateinit var dungeonTiles: TileSet
    private lateinit var dungeonDrawList: DrawList
    private lateinit var mobTiles: TileSet
    private lateinit var mobDrawList: DrawList
    private lateinit var uiTiles: TileSet
    private lateinit var uiDrawList: DrawList

    private var level: Level? = null
    private var stride: Double = 0.01
    private var pixelStride: Double = 0.01

    var cursorPosition: XY? = null

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
        val topBarSpace = 96
        val glX = ((touchX - androidViewLocation.x) / width) * 2.0 - 1.0
        val glY = ((touchY - androidViewLocation.y - topBarSpace) / (height - topBarSpace)) * 2.0 - 1.0
        val col = ((glX * aspectRatio) + stride * 0.5) / stride + pov.x
        val row = (glY + stride * 0.5) / stride + pov.y
        Timber.d("------>   $col $row  ///  $glX $glY ")
        return XY(col.toInt(), row.toInt())
    }

    override fun onSurfaceCreated(p0: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        allocateResources()
    }

    override fun onSurfaceChanged(p0: GL10?, newWidth: Int, newHeight: Int) {
        width = newWidth
        height = newHeight

        GLES20.glViewport(0, 0, width, height)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GLES20.GL_BLEND)

        updateSurfaceParams()
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        dungeonDrawList.clear()
        mobDrawList.clear()
        uiDrawList.clear()

        level?.also { level ->
            level.updateVisibility()
            // TODO: optimize: only add onscreen quads
            for (tx in 0 until level.width) {
                for (ty in 0 until level.height) {
                    val vis = level.visibilityAt(tx, ty)
                    if (vis > 0f) {
                        val textureIndex = dungeonTiles.getIndex(
                            level.tiles[tx][ty],
                            level, tx, ty
                        )
                        dungeonDrawList.addTileQuad(
                            tx - pov.x, ty - pov.y, stride,
                            textureIndex,
                            vis,
                            aspectRatio
                        )
                    }
                }
            }
        }

        dungeonDrawList.draw()

        mobDrawList.addTileQuad(0, 0, stride, mobTiles.getIndex(PLAYER), 1f, aspectRatio)
        mobDrawList.draw()

        cursorPosition?.also { cursorPosition ->
            uiDrawList.addTileQuad(cursorPosition.x - pov.x, cursorPosition.y - pov.y, stride,
                uiTiles.getIndex(CURSOR), 1f, aspectRatio)
        }
        uiDrawList.draw()
    }

    private fun updateSurfaceParams() {
        aspectRatio = width.toDouble() / height.toDouble()
        stride = 1.0 / (height.coerceAtLeast(400).toDouble() * 0.01f) * zoom
        pixelStride = height / (2.0 / stride)
    }

    private fun allocateResources() {
        dungeonTiles = DungeonTileSet(context)
        dungeonDrawList = DrawList(tileVertShader(), tileFragShader(), dungeonTiles)

        mobTiles = MobTileSet(context)
        mobDrawList = DrawList(tileVertShader(), tileFragShader(), mobTiles)

        uiTiles = UITileSet(context)
        uiDrawList = DrawList(tileVertShader(), tileFragShader(), uiTiles)
    }

}
