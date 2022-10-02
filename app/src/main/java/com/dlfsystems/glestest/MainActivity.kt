package com.dlfsystems.glestest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dlfsystems.glestest.cartos.SimpleCarto
import com.dlfsystems.glestest.render.tileview.TileView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        setContentView(R.layout.activity_main)

        val tileView = findViewById<TileView>(R.id.tile_view)
        val testLevel = SimpleCarto.makeLevel()
        tileView.observeLevel(testLevel)
        tileView.moveCenter(7, 7)

        CoroutineScope(Dispatchers.Default).launch {
            tileView.clicks.collectLatest { xy ->
                if (testLevel.isWalkableAt(xy.x, xy.y)) {
                    tileView.moveCenter(xy.x, xy.y)
                }
            }
        }
    }
}
