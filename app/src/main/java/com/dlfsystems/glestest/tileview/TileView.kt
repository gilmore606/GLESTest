package com.dlfsystems.glestest.tileview

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.dlfsystems.glestest.Level

class TileView(context: Context, attrs: AttributeSet) :
    GLSurfaceView(context, attrs) {

    private val renderer = TileRenderer(context)

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var zoom = renderer.zoom * detector.scaleFactor
            zoom = Math.max(0.5, Math.min(zoom, 5.0))
            renderer.zoom = zoom
            invalidate()
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        setRenderer(renderer)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleDetector.onTouchEvent(event)
        return true
    }

    fun observeLevel(level: Level) {
        renderer.observeLevel(level)
    }

}
