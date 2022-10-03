package com.dlfsystems.glestest

import com.dlfsystems.glestest.util.Tile.*
import com.dlfsystems.glestest.render.castShadows
import com.dlfsystems.glestest.util.DijkstraMap
import com.dlfsystems.glestest.util.Tile
import com.dlfsystems.glestest.util.XY

class Level(val width: Int, val height: Int) {

    var pov = XY(0, 0)
        set(value) {
            field = value
            isVisibilityDirty = true
            updateStepMap()
        }
    private var isVisibilityDirty = false
    private val stepMap = DijkstraMap(this)

    val tiles = Array(width) { Array(height) { WALL } }
    val visible = Array(width) { Array(height) { false } }
    val seen = Array(width) { Array(height) { false } }

    fun setTile(x: Int, y: Int, tile: Tile) {
        tiles[x][y] = tile
    }

    fun getTile(x: Int, y: Int) = try {
        tiles[x][y]
    } catch (e: ArrayIndexOutOfBoundsException) { FLOOR }

    fun getTile(xy: XY) = getTile(xy.x, xy.y)

    fun updateVisibility() {
        if (false) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    visible[x][y] = true
                    seen[x][y] = true
                }
            }
            return
        }
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

    fun updateStepMap() {
        stepMap.update(this.pov)
    }

    fun getPathToPOV(from: XY): List<XY> = stepMap.pathToZero(from)

    fun isWalkableAt(x: Int, y: Int): Boolean = try {
        tiles[x][y] == FLOOR
    } catch (e: ArrayIndexOutOfBoundsException) { false }

    fun isReachableAt(x: Int, y: Int): Boolean = try {
        stepMap.map[x][y] >= 0
    } catch (e: ArrayIndexOutOfBoundsException) { false }

    fun visibilityAt(x: Int, y: Int): Float = try {
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
