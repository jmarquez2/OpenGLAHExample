package com.jrms.openglah

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.jrms.openglah.renderers.RendererAirHockey
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var rendererSurface : RendererAirHockey? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openglSurface.setEGLContextClientVersion(3)
        rendererSurface = RendererAirHockey(this)
        openglSurface.setRenderer(rendererSurface)
        openglSurface.setOnTouchListener { view, motionEvent ->
            if(motionEvent != null){
                val normalizedX = (motionEvent.x/ view.width) * 2 - 1
                val normalizedY = - ((motionEvent.y / view.height) * 2 - 1)

                if(motionEvent.action == MotionEvent.ACTION_DOWN){
                    openglSurface.queueEvent {
                        rendererSurface!!.handleTouchEvent(normalizedX, normalizedY)

                    }
                }else if (motionEvent.action == MotionEvent.ACTION_MOVE){
                    openglSurface.queueEvent {
                        rendererSurface!!.handleTouchDrag(normalizedX, normalizedY)
                    }
                }
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    override fun onPause() {
        super.onPause()
        if(rendererSurface != null){
            openglSurface.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if(rendererSurface != null){
            openglSurface.onResume()
        }
    }
}
