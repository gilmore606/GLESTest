package com.dlfsystems.glestest

import com.dlfsystems.glestest.Tile.*
import timber.log.Timber
import java.lang.RuntimeException

class Level(val width: Int, val height: Int) {

    var pov = XY(0, 0)
        set(value) {
            field = value
            updateVisibility()
        }

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

    private fun resetVisibility() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                visibility[x][y] = 0.5f
            }
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
        } catch (e: ArrayIndexOutOfBoundsException) {
            return true
        }
    }

    private fun updateVisibility(distance: Int = 8) {

        class Shadow(atStart: Float, atEnd: Float) {
            var start = atStart
            var end = atEnd
            fun contains(other: Shadow) = (start <= other.start && end >= other.end)
        }

        class ShadowLine(val shadows: MutableList<Shadow> = mutableListOf()) {
            fun reset() { shadows.clear() }
            fun isInShadow(projection: Shadow) =
                shadows.firstOrNull { it.contains(projection) }?.let { true } ?: false

            fun isFullShadow() =
                (shadows.size == 1 && shadows[0].start == 0f && shadows[0].end == 1f)

            fun add(shadow: Shadow) {
                val i: Int = shadows.indexOfFirst { it.start >= shadow.start }
                val index = if (i == -1) shadows.size else i
                var overlapPrevious: Shadow? = null
                if (index > 0 && shadows[index - 1].end > shadow.start) {
                    overlapPrevious = shadows[index - 1]
                }
                var overlapNext: Shadow? = null
                if (index < shadows.size && shadows[index].start < shadow.end) {
                    overlapNext = shadows[index]
                }
                overlapNext?.also { next ->
                    overlapPrevious?.also { previous ->
                        previous.end = next.end
                        shadows.removeAt(index)
                    } ?: run {
                        next.start = shadow.start
                    }
                } ?: run {
                    overlapPrevious?.also { previous ->
                        previous.end = shadow.end
                    } ?: run {
                        shadows.add(index, shadow)
                    }
                }
            }
        }

        fun projectTile(row: Int, col: Int) = Shadow(
            col.toFloat() / (row.toFloat() + 2f),
            (col.toFloat() + 1f) / (row.toFloat() + 1f)
        )

        fun transformOctant(row: Int, col: Int, octant: Int) = when (octant) {
            0 -> XY(col, -row)
            1 -> XY(row, -col)
            2 -> XY(row, col)
            3 -> XY(col, row)
            4 -> XY(-col, row)
            5 -> XY(-row, col)
            6 -> XY(-row, -col)
            7 -> XY(-col, -row)
            else -> throw RuntimeException()
        }

        fun refreshOctant(pov: XY, octant: Int) {
            val line = ShadowLine()
            var fullShadow = false
            var row = 0
            var done = false
            while (!done) {
                row++
                var pos = pov + transformOctant(row, 0, octant)
                if (pov.distanceTo(pos) > distance.toFloat()) {
                    done = true
                } else {
                    var doneRow = false
                    var col = -1
                    while (!doneRow && col <= row) {
                        col++
                        pos = pov + transformOctant(row, col, octant)
                        if (pov.distanceTo(pos) > distance.toFloat()) {
                            doneRow = true
                        } else {
                            if (fullShadow) {
                                setTileVisibility(pos.x, pos.y, 0f)
                            } else {
                                val projection = projectTile(row, col)
                                val visible = !line.isInShadow(projection)
                                setTileVisibility(pos.x, pos.y, if (visible) 1f else 0f)
                                if (visible && isOpaqueAt(pos.x, pos.y)) {
                                    line.add(projection)
                                    fullShadow = line.isFullShadow()
                                }
                            }
                        }
                    }
                }
            }
        }

        resetVisibility()

        for (octant in 0..7) {
            refreshOctant(pov, octant)
        }
    }
}
