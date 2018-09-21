package com.jrms.openglah.models

import com.jrms.openglah.helpers.ColorProgram
import com.jrms.openglah.helpers.ShaderProgram
import com.jrms.openglah.helpers.VertexArray
import com.jrms.openglah.models.geometry.Cylinder
import com.jrms.openglah.models.geometry.Point

class Puck (val radius : Float, val height : Float, pointsAroundPuck : Int){

    private val COMPONENT_COUNT = 3
    private val generatedData = ObjectBuilder.createPuck(Cylinder(Point(0f, 0f, 0f), radius, height), pointsAroundPuck)
    private val vertexArray = VertexArray(generatedData.vertices)
    private val commandList = generatedData.commandDrawList

    fun bindData(colorShaderProgram: ColorProgram)
    {
        vertexArray.setVertexAttribPointer(0, colorShaderProgram.getPositionAttribLocation(),
                COMPONENT_COUNT, 0)
    }

    fun draw(){
        for(drawCommand in commandList){
            drawCommand()
        }
    }
}