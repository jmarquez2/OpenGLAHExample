package com.jrms.openglah.helpers

import android.content.Context
import android.opengl.GLES31.*;
import com.jrms.openglah.R

class ColorProgram(context: Context) : ShaderProgram(context, R.raw.vertex_simple_shader, R.raw.fragment_simple_shader){

    val uMatrixLocation = glGetUniformLocation(program, uMatrix)

    val uPositionLocation = glGetAttribLocation(program, vPosition)
    //val uColorLocation = glGetAttribLocation(program, vColor)
    val uColorLocation = glGetUniformLocation(program, uColor)

    fun getPositionAttribLocation(): Int {
        return uPositionLocation
    }

    /*fun getColorAttribLocation(): Int {
        return uColorLocation
    }*/

    fun setUniforms(matrix : FloatArray, r : Float, g : Float, b : Float){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glUniform4f(uColorLocation, r, g,b, 1f)
    }
}