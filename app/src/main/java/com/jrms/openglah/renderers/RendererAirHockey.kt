package com.jrms.openglah.renderers

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import com.jrms.openglah.R
import com.jrms.openglah.helpers.ColorProgram
import com.jrms.openglah.helpers.TextureProgram
import com.jrms.openglah.helpers.Util.Companion.loadTexture
import com.jrms.openglah.models.Mallet
import com.jrms.openglah.models.Puck
import com.jrms.openglah.models.Table
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RendererAirHockey(val context: Context) : GLSurfaceView.Renderer {



    private var projectionMatrix =  FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)


    lateinit var table :Table
    lateinit var mallet: Mallet
    lateinit var puck : Puck
    lateinit var textureProgram: TextureProgram
    lateinit var colorProgram : ColorProgram
    var texture : Int = 0

    override fun onDrawFrame(p0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        positionTableInScene()
        textureProgram.useProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        positionObjectInScene(0f, mallet.height/2f, -0.4f)
        colorProgram.useProgram()
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorProgram)
        mallet.draw()

        positionObjectInScene(0f, mallet.height/2f, 0.4f)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.draw()

        positionObjectInScene(0f, puck.height/2, 0f)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()




    }

    private fun positionObjectInScene(x: Float, y: Float,  z: Float) {
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0 , x,y,z)
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    private fun positionTableInScene() {
        setIdentityM(modelMatrix, 0)
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)

    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        glViewport(0, 0,width, height)
        /*setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, 0f, 0f, -2f)
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)
        getPerspectiveMatrix(width, height, 45f)
        val temporalMatrix = FloatArray(16)
        multiplyMM(temporalMatrix, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temporalMatrix, 0, projectionMatrix, 0, temporalMatrix.size)*/
        getPerspectiveMatrix(width, height, 45f)
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f
        ,0f, 1.0f, 0f)

    }

    private fun getPerspectiveMatrix(width: Int, height: Int, degrees : Float) {
        perspectiveM(projectionMatrix, 0, degrees, width.toFloat() / height.toFloat(),
                1f, 10f)
    }

    private fun getOrthogonalMatrix(width: Int, height: Int ) {
        val aspectRatio : Float = if(width > height){
            width.toFloat() / height.toFloat()
        }else{
            height.toFloat() / height.toFloat()
        }

        if(width > height){
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        }else{
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }

    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        textureProgram = TextureProgram(context)
        colorProgram = ColorProgram(context)

        texture = loadTexture(context, R.drawable.air_hockey_surface)
    }
}
