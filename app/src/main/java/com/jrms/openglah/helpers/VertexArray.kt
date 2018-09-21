package com.jrms.openglah.helpers


import android.opengl.GLES31.*
import com.jrms.openglah.helpers.Util.Companion.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray (val vertexData : FloatArray){
    val floatBuffer : FloatBuffer = ByteBuffer.allocateDirect(vertexData.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(vertexData)

    fun setVertexAttribPointer(dataOffset : Int, attribLocation : Int, componetCount : Int, stride : Int){
        floatBuffer.position(dataOffset)
        glVertexAttribPointer(attribLocation, componetCount, GL_FLOAT, false, stride, floatBuffer)
        glEnableVertexAttribArray(attribLocation)

        floatBuffer.position(0)
    }
}