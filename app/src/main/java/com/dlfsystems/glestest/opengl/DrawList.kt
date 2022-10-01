package com.dlfsystems.glestest.opengl

import android.opengl.GLES20
import com.dlfsystems.glestest.Tile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class DrawList(
    vertexCode: String,
    fragmentCode: String,
    private val tileSet: TileSet
) {
    var aspectRatio = 1.0

    private val COORDS_PER_VERTEX = 2
    private val VERTEX_STRIDE = COORDS_PER_VERTEX * 4
    private val MAX_QUADS = 10000

    private var vbo: FloatBuffer = ByteBuffer.allocateDirect(MAX_QUADS * VERTEX_STRIDE * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer()
    }
    private var tbo: FloatBuffer = ByteBuffer.allocateDirect(MAX_QUADS * 2 * 4 * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer()
    }
    private var vertCount = 0
    private var program = -1
    private var shaderPositionHandle = -1
    private var shaderTexcoordHandle = -1

    init {
        program = GLES20.glCreateProgram()
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertexCode)
        GLES20.glCompileShader(vertexShader)

        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentCode)
        GLES20.glCompileShader(fragmentShader)

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        GLES20.glUseProgram(program)

        shaderPositionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        shaderTexcoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoordinate")
    }

    fun clear() {
        vbo.clear()
        tbo.clear()
        vertCount = 0
    }

    fun addQuad(ix0: Double, iy0: Double, ix1: Double, iy1: Double, tile: Tile) {
        val x0 = (ix0 / aspectRatio).toFloat()
        val y0 = (-iy0).toFloat()
        val x1 = (ix1 / aspectRatio).toFloat()
        val y1 = (-iy1).toFloat()

        val tileX = tileSet.getTileX(tile)
        val tileY = tileSet.getTileY(tile)
        val tx0 = tileX * tileSet.tileRowStride
        val tx1 = tx0 + tileSet.tileRowStride
        val ty0 = tileY * tileSet.tileColumnStride
        val ty1 = ty0 + tileSet.tileColumnStride

        vbo.put(x0) // doing this one float at a time to avoid allocating another buffer
        vbo.put(y0)
        vbo.put(x0)
        vbo.put(y1)
        vbo.put(x1)
        vbo.put(y0)
        tbo.put(tx0)
        tbo.put(ty0)
        tbo.put(tx0)
        tbo.put(ty1)
        tbo.put(tx1)
        tbo.put(ty0)
        vbo.put(x1)
        vbo.put(y0)
        vbo.put(x0)
        vbo.put(y1)
        vbo.put(x1)
        vbo.put(y1)
        tbo.put(tx1)
        tbo.put(ty0)
        tbo.put(tx0)
        tbo.put(ty1)
        tbo.put(tx1)
        tbo.put(ty1)

        vertCount += 6
    }

    fun draw() {
        GLES20.glUseProgram(program)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tileSet.textureHandle)
        GLES20.glUniform1i(tileSet.textureHandle, 0)

        vbo.position(0)
        GLES20.glVertexAttribPointer(shaderPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vbo)
        GLES20.glEnableVertexAttribArray(shaderPositionHandle)
        tbo.position(0)
        GLES20.glVertexAttribPointer(shaderTexcoordHandle, 2, GLES20.GL_FLOAT, false, 0, tbo)
        GLES20.glEnableVertexAttribArray(shaderTexcoordHandle)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertCount)
    }
}
