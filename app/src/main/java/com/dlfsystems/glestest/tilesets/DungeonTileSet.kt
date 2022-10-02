package com.dlfsystems.glestest.tilesets

import android.content.Context
import com.dlfsystems.glestest.R
import com.dlfsystems.glestest.util.Tile
import com.dlfsystems.glestest.render.TileSet
import com.dlfsystems.glestest.tileholders.SimpleTile

fun DungeonTileSet(context: Context) =
    TileSet(R.drawable.tiles_dungeon, 10, 10, context).apply {
        setTile(Tile.FLOOR, SimpleTile(this, 6, 0))
        setTile(Tile.WALL, SimpleTile(this, 1, 0))
        setTile(Tile.CLOSED_DOOR, SimpleTile(this, 6, 3))
        setTile(Tile.OPEN_DOOR, SimpleTile(this, 6, 4))
    }
