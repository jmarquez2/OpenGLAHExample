package com.jrms.openglah.helpers

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES31.*
import android.opengl.GLUtils.texImage2D
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.lang.StringBuilder


class Util{
    companion object {

        const val BYTES_PER_FLOAT = 4

        fun loadShader (type : Int, shaderCode : String) : Int{
            val shader = glCreateShader(type)
            glShaderSource(shader, shaderCode)
            glCompileShader(shader)
            return shader

        }

        fun getShaderFromResource(resource : Int, context: Context) : String{
            val stringBuilder = StringBuilder()
            var inputStream : InputStream? = null
            try{
                inputStream = context.resources.openRawResource(resource)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                var newLine : String? = bufferedReader.readLine()
                do{
                    stringBuilder.append(newLine)
                    newLine = bufferedReader.readLine()
                }while (newLine != null)
            }catch (e : IOException){
                e.printStackTrace()
            }finally {
                inputStream?.close()
            }
            return stringBuilder.toString()
        }

        fun compileVertexShader (code : String) : Int{
            return compileShaderProgram(GL_VERTEX_SHADER, code)
        }

        fun compileFragmentShader (code  :String) : Int{
            return compileShaderProgram(GL_FRAGMENT_SHADER, code)
        }

        fun buildProgram(vertexShaderSource : String, fragmentShaderSource : String) : Int
        {
            val vertexShader = compileVertexShader(vertexShaderSource)
            val fragmentShader = compileFragmentShader(fragmentShaderSource)

            return linkProgram(vertexShader, fragmentShader)
        }

        private fun compileShaderProgram(type : Int, code : String) : Int{
            val shaderObject = glCreateShader(type)
            if(shaderObject == 0){
                throw RuntimeException("No se pudo crear objecto sl")
            }
            glShaderSource(shaderObject, code)
            glCompileShader(shaderObject)

            val compileStatus = IntArray(1)
            glGetShaderiv(shaderObject, GL_COMPILE_STATUS, compileStatus, 0)
            if(compileStatus[0] == 0){
                val error = glGetShaderInfoLog(shaderObject)
                glDeleteShader(shaderObject)
                throw RuntimeException("Compilation error: \n$error")
            }
            return shaderObject
        }

        private fun linkProgram (vertexId : Int , fragmentID:  Int) : Int{
            val program = glCreateProgram()
            if(program == 0){
                throw RuntimeException("No se pudo crear ")
            }
            glAttachShader(program, vertexId)
            glAttachShader(program, fragmentID)
            glLinkProgram(program)

            val linkStatus = IntArray(1)
            glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0)
            if(linkStatus[0] == 0){
                glDeleteProgram(program)
                val error = glGetProgramInfoLog(program)
                throw IllegalStateException("Failed linking program: \n$error")
            }
            return program
        }

        fun isProgramValid(programID : Int) : Boolean{
            glValidateProgram(programID)
            val validateStatus = IntArray(1)
            glGetProgramiv(programID, GL_VALIDATE_STATUS, validateStatus, 0)
            val notValid = validateStatus[0] == 0
            if(notValid){
                val error = glGetProgramInfoLog(programID)
                Log.e("Programa no válido", error)
            }
            return !notValid
        }
        fun loadTexture(context: Context, resourceId : Int) : Int{
            val textureObjectIds = IntArray(1)
            glGenTextures(1, textureObjectIds, 0)

            if(textureObjectIds[0] == 0){
                throw RuntimeException("Error al generar objetos de textura")
            }

            val options = BitmapFactory.Options()
            options.inScaled = false

            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
            if(bitmap == null){
                glDeleteTextures(1, textureObjectIds, 0)
                throw IllegalArgumentException("El recurso no existe")
            }
            glBindTexture(GL_TEXTURE_2D, textureObjectIds[0])
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

            texImage2D(GL_TEXTURE_2D, 0 , bitmap,0 )
            bitmap.recycle()
            glGenerateMipmap(GL_TEXTURE_2D)

            glBindTexture(GL_TEXTURE_2D, 0)

            return textureObjectIds[0]
        }
    }


}