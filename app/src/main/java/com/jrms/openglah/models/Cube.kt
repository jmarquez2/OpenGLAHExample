package com.jrms.openglah.models

import android.opengl.GLES20
import com.jrms.openglah.helpers.Util.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube {

    /** Cube vertices  */
    private val VERTICES = floatArrayOf(-0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f)

    /** Vertex colors.  */
    private val COLORS = floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f)


    /** Order to draw vertices as triangles.  */
    private val INDICES = byteArrayOf(0, 1, 3, 3, 1, 2, // Front face.
            0, 1, 4, 4, 5, 1, // Bottom face.
            1, 2, 5, 5, 6, 2, // Right face.
            2, 3, 6, 6, 7, 3, // Top face.
            3, 7, 4, 4, 3, 0, // Left face.
            4, 5, 7, 7, 6, 5)// Rear face.

    /** Number of coordinates per vertex in [VERTICES].  */
    private val COORDS_PER_VERTEX = 3

    /** Number of values per colors in [COLORS].  */
    private val VALUES_PER_COLOR = 4

    /** Vertex size in bytes.  */
    private val VERTEX_STRIDE = COORDS_PER_VERTEX * 4

    /** Color size in bytes.  */
    private val COLOR_STRIDE = VALUES_PER_COLOR * 4

    /** Shader code for the vertex.  */
    private val VERTEX_SHADER_CODE = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 vColor;" +
            "varying vec4 _vColor;" +
            "void main() {" +
            "  _vColor = vColor;" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}"

    /** Shader code for the fragment.  */
    private val FRAGMENT_SHADER_CODE = (
            "precision mediump float;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  gl_FragColor = _vColor;" +
                    "}")


    private val mVertexBuffer: FloatBuffer
    private val mColorBuffer: FloatBuffer
    private val mIndexBuffer: ByteBuffer
    private val mProgram: Int
    private val mPositionHandle: Int
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int

    init {
        var byteBuffer = ByteBuffer.allocateDirect(VERTICES.size * 4)

        byteBuffer.order(ByteOrder.nativeOrder())
        mVertexBuffer = byteBuffer.asFloatBuffer()
        mVertexBuffer.put(VERTICES)
        mVertexBuffer.position(0)

        byteBuffer = ByteBuffer.allocateDirect(COLORS.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        mColorBuffer = byteBuffer.asFloatBuffer()
        mColorBuffer.put(COLORS)
        mColorBuffer.position(0)

        mIndexBuffer = ByteBuffer.allocateDirect(INDICES.size)
        mIndexBuffer.put(INDICES)
        mIndexBuffer.position(0)

        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE))
        GLES20.glAttachShader(
                mProgram, loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE))
        GLES20.glLinkProgram(mProgram)

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor")
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix The Model View Project matrix in which to draw this shape
     */
    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL environment.
        GLES20.glUseProgram(mProgram)

        // Prepare the cube coordinate data.
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
                mPositionHandle, 3, GLES20.GL_FLOAT, false, VERTEX_STRIDE, mVertexBuffer)

        // Prepare the cube color data.
        GLES20.glEnableVertexAttribArray(mColorHandle)
        GLES20.glVertexAttribPointer(
                mColorHandle, 4, GLES20.GL_FLOAT, false, COLOR_STRIDE, mColorBuffer)

        // Apply the projection and view transformation.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the cube.
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, INDICES.size, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer)

        // Disable vertex arrays.
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mColorHandle)
    }



}