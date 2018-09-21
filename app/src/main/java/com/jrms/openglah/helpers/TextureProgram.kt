package com.jrms.openglah.helpers

import android.content.Context

import android.opengl.GLES31.*
import com.jrms.openglah.R

class TextureProgram(context: Context ) : ShaderProgram(context, R.raw.texture_vertex, R.raw.texture_fragment){

    var uMatrixLocation = glGetUniformLocation(program, uMatrix)
    var uTextureUnitLocation = glGetUniformLocation(program, uTextureUnit)

    var uPositionLocation = glGetAttribLocation(program, vPosition)
    var vTexturePositionLocation = glGetAttribLocation(program, vTextureCoordinates)

    fun getPositionAttribLocation(): Int {
        return uPositionLocation
    }

    fun getTextureCordinatesAttribLocation(): Int {
        return vTexturePositionLocation
    }

    fun setUniforms(matrix : FloatArray, textureID : Int){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureID)
        glUniform1i(uTextureUnitLocation, 0)
    }
}