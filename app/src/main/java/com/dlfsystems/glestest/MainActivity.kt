package com.dlfsystems.glestest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dlfsystems.glestest.cartos.SimpleCarto
import com.dlfsystems.glestest.tileview.TileView
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        setContentView(R.layout.activity_main)

        val tileView = findViewById<TileView>(R.id.tile_view)
        val testLevel = SimpleCarto.makeLevel()
        tileView.observeLevel(testLevel)
    }
}
