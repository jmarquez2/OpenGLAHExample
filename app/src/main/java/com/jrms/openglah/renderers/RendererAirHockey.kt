package com.jrms.openglah.renderers

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import android.support.v4.math.MathUtils.clamp
import android.util.Log
import com.jrms.openglah.R
import com.jrms.openglah.helpers.ColorProgram
import com.jrms.openglah.helpers.TextureProgram
import com.jrms.openglah.helpers.Util.Companion.intersectionPoint
import com.jrms.openglah.helpers.Util.Companion.intersects
import com.jrms.openglah.helpers.Util.Companion.loadTexture
import com.jrms.openglah.helpers.Util.Companion.vectorBetween
import com.jrms.openglah.models.Mallet
import com.jrms.openglah.models.Puck
import com.jrms.openglah.models.Table
import com.jrms.openglah.models.Vector
import com.jrms.openglah.models.geometry.Plane
import com.jrms.openglah.models.geometry.Point
import com.jrms.openglah.models.geometry.Ray
import com.jrms.openglah.models.geometry.Sphere
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RendererAirHockey(val context: Context) : GLSurfaceView.Renderer {



    private var projectionMatrix =  FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private val invertedProjectionMatrix = FloatArray(16)

    private val leftBound = -0.5f
    private val rightBound = 0.5f
    private val farBound = -0.8f
    private val nearBound = 0.8f


    private lateinit var table :Table
    private lateinit var mallet: Mallet
    private lateinit var puck : Puck
    private lateinit var textureProgram: TextureProgram
    private lateinit var colorProgram : ColorProgram
    private var texture : Int = 0
    private var malletPressed : Boolean = false
    private lateinit var blueMalletPosition : Point
    private lateinit var previousBlueMalletPosition : Point
    private lateinit var puckPosition : Point;
    private lateinit var puckVector : Vector

    override fun onDrawFrame(p0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        puckPosition = puckPosition.translate(puckVector)

        if(puckPosition.x < leftBound + puck.radius
        || puckPosition.x > rightBound - puck.radius){
            puckVector = Vector(-puckVector.x, puckVector.y, puckVector.z)
            puckVector = puckVector.scale(0.99f)
        }
        if(puckPosition.z < farBound + puck.radius || puckPosition.z >
                nearBound - puck.radius){
            puckVector = Vector(puckVector.x, puckVector.y, -puckVector.z)
            puckVector = puckVector.scale(0.99f)
        }

        puckPosition = Point(clamp(puckPosition.x, leftBound + puck.radius,
                rightBound - puck.radius), puckPosition.y, clamp(puckPosition.z,
                farBound + puck.radius, nearBound - puck.radius))
        puckVector = puckVector.scale(0.99f)


        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        invertM(invertedProjectionMatrix, 0, viewProjectionMatrix, 0)
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

        //positionObjectInScene(0f, mallet.height/2f, 0.4f)
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.draw()

        //positionObjectInScene(0f, puck.height/2, 0f)
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z)
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
        blueMalletPosition = Point(0f, mallet.height / 2f, 0.4f)
        puck = Puck(0.06f, 0.02f, 32)
        puckPosition = Point(0f, puck.height/2f, 0f)
        puckVector = Vector(0f, 0f, 0f)
        textureProgram = TextureProgram(context)
        colorProgram = ColorProgram(context)

        texture = loadTexture(context, R.drawable.air_hockey_surface)
    }

    fun handleTouchEvent(normalizedX: Float, normalizedY: Float) {
        Log.d("X in touch", normalizedX.toString())
        Log.d("Y in touch", normalizedY.toString())

        val ray = convertToNormalized2DPointToRay(normalizedX, normalizedY)

        val malletSphere = Sphere(Point(blueMalletPosition.x,
                blueMalletPosition.y, blueMalletPosition.z), mallet.height/2f)

        malletPressed = intersects(malletSphere, ray)
    }

    fun handleTouchDrag(normalizedX: Float, normalizedY: Float) {
        if(malletPressed){
            val ray = convertToNormalized2DPointToRay(normalizedX, normalizedY)
            val plane = Plane(Point(0f, 0f, 0f), Vector(0f, 1f, 0f))

            val touchedPoint = intersectionPoint(ray, plane)
            previousBlueMalletPosition = blueMalletPosition
            //blueMalletPosition = Point(touchedPoint.x, mallet.height / 2f, touchedPoint.z)
            blueMalletPosition = Point(clamp(touchedPoint.x, leftBound + mallet.radius,
                    rightBound - mallet.radius), mallet.height / 2f, clamp(touchedPoint.z,
                    0f + mallet.radius, nearBound - mallet.radius))

            val distance = vectorBetween(blueMalletPosition, puckPosition).length()

            if(distance < (puck.radius + mallet.radius)){
                puckVector = vectorBetween(previousBlueMalletPosition, blueMalletPosition)
            }
        }
    }

    private fun convertToNormalized2DPointToRay(x : Float, y : Float) : Ray {
        val nearPointNdc = floatArrayOf(x, y, -1f, 1f)
        val farPointNdc = floatArrayOf(x, y, 1f, 1f)

        var nearPointWorld = FloatArray(4)
        var farPointWorld = FloatArray(4)

        multiplyMV(nearPointWorld, 0, invertedProjectionMatrix, 0, nearPointNdc, 0)
        multiplyMV(farPointWorld, 0, invertedProjectionMatrix, 0, farPointNdc, 0)

        nearPointWorld = divideByW(nearPointWorld)
        farPointWorld = divideByW(farPointWorld)

        val nearPointRay = Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2])
        val farPointRay = Point(farPointWorld[0], farPointWorld[1], farPointWorld[2])

        return Ray(nearPointRay, vectorBetween(nearPointRay, farPointRay))
    }

    fun divideByW(vector : FloatArray) : FloatArray{
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]

        return vector
    }


}
