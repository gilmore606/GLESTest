package com.dlfsystems.glestest.tilesets

import android.content.Context
import com.dlfsystems.glestest.R
import com.dlfsystems.glestest.util.Tile
import com.dlfsystems.glestest.render.TileSet
import com.dlfsystems.glestest.tileholders.SimpleTile

fun UITileSet(context: Context) =
    TileSet(R.drawable.tiles_ui, 1, 1, context).apply {
        setTile(Tile.CURSOR, SimpleTile(this, 0, 0))
    }
