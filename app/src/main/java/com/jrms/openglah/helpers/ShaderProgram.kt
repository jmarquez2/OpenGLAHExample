package com.jrms.openglah.helpers

import android.content.Context
import android.opengl.GLES31.glUseProgram
import com.jrms.openglah.helpers.Util.Companion.buildProgram
import com.jrms.openglah.helpers.Util.Companion.getShaderFromResource

 open class ShaderProgram protected constructor(context: Context, vertexResourceId : Int,
                                               fragmentShaderId : Int){
     protected val uMatrix = "u_Matrix"
     protected val uTextureUnit = "u_TextureUnit"
     protected val vPosition = "a_Position"
     protected val vColor = "a_Color"
     protected val vTextureCoordinates = "a_TextureCoordinates"
     protected val uColor = "u_Color"


     protected val program = buildProgram(getShaderFromResource(vertexResourceId, context),
            getShaderFromResource(fragmentShaderId, context))

     fun useProgram(){
         glUseProgram(program)
     }


}