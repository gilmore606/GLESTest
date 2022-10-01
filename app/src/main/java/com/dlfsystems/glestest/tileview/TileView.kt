package com.dlfsystems.glestest.tileview

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.dlfsystems.glestest.Level
import com.dlfsystems.glestest.XY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class TileView(context: Context, attrs: AttributeSet) :
    GLSurfaceView(context, attrs) {

    val clicks = MutableSharedFlow<XY>()

    private val renderer = TileRenderer(context)
    private var lastScaleTime = 0L
    private var lastDownTime = 0L

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var zoom = renderer.zoom * detector.scaleFactor
            zoom = Math.max(0.5, Math.min(zoom, 8.0))
            renderer.zoom = zoom
            invalidate()
            lastScaleTime = System.currentTimeMillis()
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        setRenderer(renderer)
    }

    fun moveCenter(newX: Int, newY: Int) {
        renderer.center = XY(newX, newY)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        renderer.viewLocation = XY(left, top) // this allocation is fine, layout happens rarely
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var handled = scaleDetector.onTouchEvent(event)
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastDownTime = System.currentTimeMillis()
                //Timber.d("DING ${event.x} ${event.y}")
                handled
            }
            MotionEvent.ACTION_UP -> {
                if (lastDownTime > lastScaleTime) {
                    CoroutineScope(Dispatchers.Default).launch {
                        clicks.emit(renderer.touchToTileXY(event.x, event.y))
                    }
                    handled = true
                }
                handled
            }
            else -> handled || super.onTouchEvent(event)
        }
    }

    fun observeLevel(level: Level) {
        renderer.observeLevel(level)
    }

}
