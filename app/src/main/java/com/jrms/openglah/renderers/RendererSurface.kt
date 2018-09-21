package com.jrms.openglah.renderers

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.os.SystemClock
import com.jrms.openglah.models.Cube
import java.util.concurrent.TimeUnit





class RendererSurface : GLSurfaceView.Renderer {

    lateinit var cube : Cube

    private val CUBE_ROTATION_INCREMENT = 0.6f

    /** The refresh rate, in frames per second.  */
    private val REFRESH_RATE_FPS = 60

    /** The duration, in milliseconds, of one frame.  */
    private val FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS

    private var mMVPMatrix: FloatArray? = null
    private var mProjectionMatrix: FloatArray? = null
    private var mViewMatrix: FloatArray? = null
    private var mRotationMatrix: FloatArray? = null
    private var mFinalMVPMatrix: FloatArray

    private var mCubeRotation: Float = 0.toFloat()
    private var mLastUpdateMillis: Long = 0

    init {
        mMVPMatrix = FloatArray(16);
        mProjectionMatrix = FloatArray(16);
        mViewMatrix = FloatArray(16);
        mRotationMatrix = FloatArray(16);
        mFinalMVPMatrix = FloatArray(16);
        // Set the fixed camera position (View matrix).
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, -4.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        // Apply the rotation.
        Matrix.setRotateM(mRotationMatrix, 0, mCubeRotation, 1.0f, 1.0f, 1.0f)
        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mFinalMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0)
        // Draw cube.
        cube.draw(mFinalMVPMatrix)
        updateCubeRotation()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        val ratio = width.toFloat() / height
        GLES20.glViewport(0, 0, width, height)
        // This projection matrix is applied to object coordinates in the onDrawFrame() method.
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f)
        // modelView = projection x view
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClearDepthf(1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        cube = Cube()


    }

    private fun updateCubeRotation() {
        if (mLastUpdateMillis != 0L) {
            val factor = (SystemClock.elapsedRealtime() - mLastUpdateMillis) / FRAME_TIME_MILLIS
            mCubeRotation += CUBE_ROTATION_INCREMENT * factor
        }
        mLastUpdateMillis = SystemClock.elapsedRealtime()
    }
}