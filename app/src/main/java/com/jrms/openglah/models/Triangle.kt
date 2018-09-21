package com.jrms.openglah.models


import android.opengl.GLES20
import com.jrms.openglah.helpers.Util
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class Triangle {
    private var positionHandler : Int = 0
    private var colorHandler : Int = 0
    private var vertexBuffer : FloatBuffer
    private var program : Int = 0
    private val COORDS_PER_VERTEX = 3;

    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"
    var triangleCoords = floatArrayOf(// in counterclockwise order:
            0.0f, 0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    )

    var color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init{

        val vertexShader = Util.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode)
        val fragmentShader = Util.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode)

        program = GLES20.glCreateProgram()

        GLES20.glAttachShader(program, vertexShader)

        GLES20.glAttachShader(program, fragmentShader)

        GLES20.glLinkProgram(program)

        val bb = ByteBuffer.allocateDirect(

                triangleCoords.size * 4)

        bb.order(ByteOrder.nativeOrder())

        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
    }

    fun draw(){

        GLES20.glUseProgram(program)

        positionHandler = GLES20.glGetAttribLocation(program, "vPosition");

        GLES20.glEnableVertexAttribArray(positionHandler);

        GLES20.glVertexAttribPointer(positionHandler, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        colorHandler = GLES20.glGetUniformLocation(program, "vColor");

        GLES20.glUniform4fv(colorHandler, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandler);
    }


}