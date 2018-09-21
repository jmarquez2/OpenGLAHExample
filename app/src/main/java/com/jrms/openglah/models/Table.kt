package com.jrms.openglah.models



import android.opengl.GLES31.*
import com.jrms.openglah.helpers.TextureProgram
import com.jrms.openglah.helpers.Util.Companion.BYTES_PER_FLOAT
import com.jrms.openglah.helpers.VertexArray


class Table() {

    private val vertices =
    floatArrayOf(
            //X,Y,S,T
            // Triangle Fan
            0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.8f,   0f, 0.9f,
            0.5f, -0.8f,   1f, 0.9f,
            0.5f,  0.8f,   1f, 0.1f,
            -0.5f,  0.8f,   0f, 0.1f,
            -0.5f, -0.8f,   0f, 0.9f


    )

    var vertexArray = VertexArray(vertices)

    companion object {
        const val COMPONENT_COUNT = 2
        const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        const val STRIDE = (COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT
        /*const val COMPONENT_COUNT = 4;
        const val COLOR_COUNT = 3
        const val STRIDE = (COMPONENT_COUNT + COLOR_COUNT) * BYTES_PER_FLOAT*/
    }


    fun bindData(textureProgram : TextureProgram){
        vertexArray.setVertexAttribPointer(0, textureProgram.getPositionAttribLocation(),
                COMPONENT_COUNT, STRIDE)
        vertexArray.setVertexAttribPointer(COMPONENT_COUNT, textureProgram.getTextureCordinatesAttribLocation(),
               TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE)
    }


    /*fun bindData(colorProgram : ColorProgram){
        vertexArray.setVertexAttribPointer(0, colorProgram.getPositionAttribLocation(),
                COMPONENT_COUNT, STRIDE)
        vertexArray.setVertexAttribPointer(COMPONENT_COUNT, colorProgram.getColorAttribLocation(),
                COLOR_COUNT, STRIDE)
    }*/

    fun draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }


}