package com.dlfsystems.glestest.render

import com.dlfsystems.glestest.XY
import java.lang.RuntimeException

fun castShadows(
    pov: XY,
    distance: Float,
    isOpaqueAt: (x: Int, y: Int) -> Boolean,
    setVisibility: (x: Int, y: Int, visibility: Boolean) -> Unit
) {

    class Shadow(atStart: Float, atEnd: Float) {
        var start = atStart
        var end = atEnd
        fun contains(other: Shadow) = (start <= other.start && end >= other.end)
    }

    class ShadowLine(val shadows: MutableList<Shadow> = mutableListOf()) {
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
            if (pov.distanceTo(pos) > distance) {
                done = true
            } else {
                var doneRow = false
                var col = 0
                while (!doneRow && col <= row) {
                    pos = pov + transformOctant(row, col, octant)
                    if (pov.distanceTo(pos) > distance) {
                        doneRow = true
                    } else {
                        if (fullShadow) {
                            setVisibility(pos.x, pos.y, false)
                        } else {
                            val projection = projectTile(row, col)
                            val visible = !line.isInShadow(projection)
                            setVisibility(pos.x, pos.y, visible)
                            if (visible && isOpaqueAt(pos.x, pos.y)) {
                                line.add(projection)
                                fullShadow = line.isFullShadow()
                            }
                        }
                    }
                    col++
                }
            }
        }
    }

    for (octant in 0..7) {
        refreshOctant(pov, octant)
    }
    setVisibility(pov.x, pov.y, true)
}
