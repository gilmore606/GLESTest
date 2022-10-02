package com.dlfsystems.glestest.tilesets

import android.content.Context
import com.dlfsystems.glestest.R
import com.dlfsystems.glestest.util.Tile
import com.dlfsystems.glestest.render.TileSet
import com.dlfsystems.glestest.tileholders.SimpleTile

fun MobTileSet(context: Context) =
    TileSet(R.drawable.tiles_mob, 7, 4, context).apply {
        setTile(Tile.PLAYER, SimpleTile(this, 2, 2))
    }
