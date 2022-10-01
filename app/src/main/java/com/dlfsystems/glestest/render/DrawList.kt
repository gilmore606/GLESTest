package com.dlfsystems.glestest.render

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

    private var vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(MAX_QUADS * VERTEX_STRIDE * 6).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer()
    }
    private var texCoordBuffer: FloatBuffer = ByteBuffer.allocateDirect(MAX_QUADS * 2 * 4 * 6).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer()
    }
    private var visibilityBuffer: FloatBuffer = ByteBuffer.allocateDirect(MAX_QUADS * 6 * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer()
    }
    private var vertCount = 0
    private var program = -1
    private var shaderPositionHandle = -1
    private var shaderTexcoordHandle = -1
    private var shaderVisibilityHandle = -1

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
        shaderVisibilityHandle = GLES20.glGetAttribLocation(program, "a_Visibility")
    }

    fun clear() {
        vertexBuffer.clear()
        texCoordBuffer.clear()
        visibilityBuffer.clear()
        vertCount = 0
    }

    fun addQuad(ix0: Double, iy0: Double, ix1: Double, iy1: Double, tile: Tile, visibility: Float) {
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

        vertexBuffer.put(x0) // doing this one float at a time to avoid allocating another buffer
        vertexBuffer.put(y0)
        vertexBuffer.put(x0)
        vertexBuffer.put(y1)
        vertexBuffer.put(x1)
        vertexBuffer.put(y0)
        texCoordBuffer.put(tx0)
        texCoordBuffer.put(ty0)
        texCoordBuffer.put(tx0)
        texCoordBuffer.put(ty1)
        texCoordBuffer.put(tx1)
        texCoordBuffer.put(ty0)
        vertexBuffer.put(x1)
        vertexBuffer.put(y0)
        vertexBuffer.put(x0)
        vertexBuffer.put(y1)
        vertexBuffer.put(x1)
        vertexBuffer.put(y1)
        texCoordBuffer.put(tx1)
        texCoordBuffer.put(ty0)
        texCoordBuffer.put(tx0)
        texCoordBuffer.put(ty1)
        texCoordBuffer.put(tx1)
        texCoordBuffer.put(ty1)
        repeat(6) {
            visibilityBuffer.put(visibility)
        }

        vertCount += 6
    }

    fun draw() {
        GLES20.glUseProgram(program)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tileSet.textureHandle)
        GLES20.glUniform1i(tileSet.textureHandle, 0)

        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(shaderPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(shaderPositionHandle)
        texCoordBuffer.position(0)
        GLES20.glVertexAttribPointer(shaderTexcoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)
        GLES20.glEnableVertexAttribArray(shaderTexcoordHandle)
        visibilityBuffer.position(0)
        GLES20.glVertexAttribPointer(shaderVisibilityHandle, 1, GLES20.GL_FLOAT, false, 0, visibilityBuffer)
        GLES20.glEnableVertexAttribArray(shaderVisibilityHandle)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertCount)
    }
}
