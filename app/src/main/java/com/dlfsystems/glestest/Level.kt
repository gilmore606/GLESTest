package com.dlfsystems.glestest

import com.dlfsystems.glestest.Tile.*
import com.dlfsystems.glestest.render.castShadows
import timber.log.Timber

class Level(val width: Int, val height: Int) {

    var pov = XY(0, 0)
        set(value) {
            field = value
            isVisibilityDirty = true
        }
    private var isVisibilityDirty = false

    val tiles = Array(width) { Array(height) { WALL } }
    val visibility = Array(width) { Array(height) { 0f } }

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
        if (isVisibilityDirty) {
            doUpdateVisibility()
            isVisibilityDirty = false
        }
    }

    private fun setTileVisibility(x: Int, y: Int, vis: Float) {
        try {
            visibility[x][y] = vis
            Timber.d("vis $x $y = $vis")
        } catch (e: ArrayIndexOutOfBoundsException) { }
    }

    private fun isOpaqueAt(x: Int, y: Int): Boolean {
        try {
            return tiles[x][y] !== FLOOR
        } catch (e: ArrayIndexOutOfBoundsException) { return true }
    }

    private fun doUpdateVisibility(distance: Int = 14) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                visibility[x][y] = 0.5f
            }
        }
        castShadows(
            pov,
            distance.toFloat(),
            { x, y -> isOpaqueAt(x, y) },
            { x, y, vis -> setTileVisibility(x, y, if (vis) 1f else 0f) }
        )
    }
}
