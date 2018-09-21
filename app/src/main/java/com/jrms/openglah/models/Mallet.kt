package com.jrms.openglah.models

import com.jrms.openglah.helpers.ColorProgram
import com.jrms.openglah.helpers.VertexArray
import com.jrms.openglah.models.geometry.Point

class Mallet(val radius : Float, val height : Float, pointsAroundMallet : Int){

    private  val COMPONENT_COUNT = 3
    private val generatedData = ObjectBuilder.createMallet(Point(0f, 0f, 0f),
            radius, height, pointsAroundMallet)
    private val vertexArray = VertexArray(generatedData.vertices)
    val drawCommandList = generatedData.commandDrawList


    fun bindData(colorProgram : ColorProgram){
        vertexArray.setVertexAttribPointer(0, colorProgram.getPositionAttribLocation(),
                COMPONENT_COUNT, 0)
    }


    fun draw(){
        for(drawCommand in drawCommandList){
            drawCommand()
        }
    }


}