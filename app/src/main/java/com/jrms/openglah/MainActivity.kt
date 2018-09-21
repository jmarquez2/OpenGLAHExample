package com.jrms.openglah

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
