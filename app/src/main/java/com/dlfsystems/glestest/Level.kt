package com.dlfsystems.glestest

import com.dlfsystems.glestest.Tile.*
import com.dlfsystems.glestest.render.castShadows

class Level(val width: Int, val height: Int) {

    var pov = XY(0, 0)
        set(value) {
            field = value
            isVisibilityDirty = true
        }
    private var isVisibilityDirty = false

    val tiles = Array(width) { Array(height) { WALL } }
    val visible = Array(width) { Array(height) { false } }
    val seen = Array(width) { Array(height) { false } }

    fun setTile(x: Int, y: Int, tile: Tile) {
        tiles[x][y] = tile
    }

    fun makeRoom(x0: Int, y0: Int, x1: Int, y1: Int, tile: Tile = FLOOR) {
        var y = y0
        while (y <= y1) {
            var x = x0
            while (x <= x1) {
                setTile(x, y, tile)
                x++
            }
            y++
        }
    }

    fun updateVisibility() {
        val distance = 12f
        if (isVisibilityDirty) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    visible[x][y] = false
                }
            }
            castShadows(
                pov, distance,
                { x, y -> isOpaqueAt(x, y) },
                { x, y, vis -> setTileVisibility(x, y, vis) }
            )
            isVisibilityDirty = false
        }
    }

    fun isWalkableAt(x: Int, y: Int): Boolean = try {
        tiles[x][y] == FLOOR
    } catch (e: ArrayIndexOutOfBoundsException) { false }

    fun renderVisibilityAt(x: Int, y: Int): Float = try {
        (if (seen[x][y]) 0.6f else 0f) + (if (visible[x][y]) 0.4f else 0f)
    } catch (e: ArrayIndexOutOfBoundsException) { 0f }

    private fun isOpaqueAt(x: Int, y: Int): Boolean = try {
        tiles[x][y] !== FLOOR
    } catch (e: ArrayIndexOutOfBoundsException) { true }

    private fun setTileVisibility(x: Int, y: Int, vis: Boolean) {
        try {
            visible[x][y] = vis
            if (vis) seen[x][y] = true
        } catch (e: ArrayIndexOutOfBoundsException) { }
    }
}
