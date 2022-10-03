package com.dlfsystems.glestest.tilesets

import android.content.Context
import com.dlfsystems.glestest.R
import com.dlfsystems.glestest.util.Tile
import com.dlfsystems.glestest.render.TileSet
import com.dlfsystems.glestest.tileholders.WallTile
import com.dlfsystems.glestest.tileholders.SimpleTile
import com.dlfsystems.glestest.tileholders.VariantsTile

fun DungeonTileSet(context: Context) =
    TileSet(R.drawable.tiles_dungeon, 10, 10, context).apply {

        setTile(Tile.FLOOR, VariantsTile(this).apply {
            add(0.1f, 6, 0)
            add(0.1f, 7, 0)
            add(0.1f, 8, 0)
            add(0.1f, 9, 0)
            add(0.1f, 6, 1)
            add(0.1f, 7, 1)
            add(0.1f, 8, 1)
            add(0.1f, 9, 1)
            add(0.1f, 6, 2)
            add(0.1f, 7, 2)
        })

        setTile(Tile.WALL, WallTile(this, Tile.WALL).apply {
            add(WallTile.Slot.TOP, 0.25f, 1, 0)
            add(WallTile.Slot.TOP, 0.25f, 2, 0)
            add(WallTile.Slot.TOP, 0.25f, 3, 0)
            add(WallTile.Slot.TOP, 0.25f, 4, 0)
            add(WallTile.Slot.LEFT, 0.25f, 0, 0)
            add(WallTile.Slot.LEFT, 0.25f, 0, 1)
            add(WallTile.Slot.LEFT, 0.25f, 0, 2)
            add(WallTile.Slot.LEFT, 0.25f, 0, 3)
            add(WallTile.Slot.RIGHT, 0.25f, 5, 0)
            add(WallTile.Slot.RIGHT, 0.25f, 5, 1)
            add(WallTile.Slot.RIGHT, 0.25f, 5, 2)
            add(WallTile.Slot.RIGHT, 0.25f, 5, 3)
            add(WallTile.Slot.LEFTBOTTOM, 1f, 0, 4)
            add(WallTile.Slot.BOTTOM, 0.25f, 1, 4)
            add(WallTile.Slot.BOTTOM, 0.25f, 2, 4)
            add(WallTile.Slot.BOTTOM, 0.25f, 3, 4)
            add(WallTile.Slot.BOTTOM, 0.25f, 4, 4)
            add(WallTile.Slot.RIGHTBOTTOM, 1f, 5, 4)
        })


        setTile(Tile.CLOSED_DOOR, SimpleTile(this, 6, 3))
        setTile(Tile.OPEN_DOOR, SimpleTile(this, 6, 4))
    }
