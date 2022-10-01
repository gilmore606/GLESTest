package com.dlfsystems.glestest

import com.dlfsystems.glestest.Tile.*
import timber.log.Timber

class Level(val width: Int, val height: Int) {

    val tiles = Array(width) { Array(height) { WALL } }

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
}
