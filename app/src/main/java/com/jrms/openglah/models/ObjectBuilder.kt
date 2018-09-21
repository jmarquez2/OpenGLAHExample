package com.jrms.openglah.models


import android.opengl.GLES31.*
import com.jrms.openglah.models.geometry.Circle
import com.jrms.openglah.models.geometry.Cylinder
import com.jrms.openglah.models.geometry.Point

class ObjectBuilder private constructor(verticesSize : Int) {
    private val FLOATS_PER_VERTEX = 3
    private val vertices = FloatArray(verticesSize * FLOATS_PER_VERTEX)
    private var offset = 0

    private val drawList = ArrayList<() -> Unit>()


    companion object {

        private fun sizeOfVerticesCircle(verticesNumber : Int) : Int{
            return 1 + verticesNumber + 1
        }

        private fun sizeOfVerticesOpenCylinder(verticesNumber : Int) : Int{
            return (verticesNumber + 1)  * 2
        }


        fun createPuck(puck : Cylinder, numPoints : Int) : GeneratedData{
            val size = sizeOfVerticesCircle(numPoints) + sizeOfVerticesOpenCylinder(numPoints)
            val objectBuilder = ObjectBuilder(size)

            val puckTop = Circle(puck.center.translateY(puck.height / 2f), puck.radius)
            objectBuilder.appendCircle(puckTop, numPoints)
            objectBuilder.appendOpenCylinder(puck, numPoints)

            return objectBuilder.build()


        }

        fun createMallet (center : Point, radius : Float, height : Float, numPoints : Int) : GeneratedData{
            val size = sizeOfVerticesCircle(numPoints) * 2 + sizeOfVerticesOpenCylinder(numPoints) * 2

            val objectBuilder = ObjectBuilder(size)
            val baseHeight : Float = height * 0.25f

            val baseCircle = Circle(center.translateY(-baseHeight), radius)
            val cylinderBase = Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius,
                    baseHeight)
            objectBuilder.appendCircle(baseCircle, numPoints)
            objectBuilder.appendOpenCylinder(cylinderBase, numPoints)

            val parallelHeight : Float = height * 0.75f
            val parallelRadius : Float = radius / 3f

            val parallelCircle = Circle(center.translateY(height * 0.5f), parallelRadius)
            val parallelCylinder = Cylinder(parallelCircle.center.translateY(-parallelHeight / 2f),
                    parallelRadius, parallelHeight)

            objectBuilder.appendCircle(parallelCircle, numPoints)
            objectBuilder.appendOpenCylinder(parallelCylinder, numPoints)

            return objectBuilder.build()

        }
    }

    private fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfVerticesOpenCylinder(numPoints)

        val yStart = cylinder.center.y - (cylinder.height / 2f)
        val yEnd = cylinder.center.y + (cylinder.height / 2f)

        for(i in 0..numPoints){
            val angle  = ((i.toFloat() / numPoints.toFloat()) * (Math.PI * 2f))
            val xPosition : Float = (cylinder.center.x + cylinder.radius * Math.cos(angle)).toFloat()
            val zPosition : Float = (cylinder.center.z + cylinder.radius * Math.sin(angle)).toFloat()

            vertices[offset++] = xPosition
            vertices[offset++] = yStart
            vertices[offset++] = zPosition

            vertices[offset++] = xPosition
            vertices[offset++] = yEnd
            vertices[offset++] = zPosition
        }

        val functionDraw  = {
            glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices)
        }

        drawList.add(functionDraw)
    }

    private fun appendCircle(circle: Circle, numPoints: Int) {
        vertices[offset++] = circle.center.x
        vertices[offset++] = circle.center.y
        vertices[offset++] = circle.center.z

        for(i in 0..numPoints){
            val angleInRadians = (i.toFloat() / numPoints.toFloat()) * (Math.PI * 2f)
            vertices[offset++] = (circle.center.x + circle.radius * Math.cos(angleInRadians)).toFloat()
            vertices[offset++] = circle.center.y
            vertices[offset++] = (circle.center.z + circle.radius * Math.sin(angleInRadians)).toFloat()

            val startVertex = offset / FLOATS_PER_VERTEX
            val numVertices = sizeOfVerticesCircle(numPoints)

            val functionDraw = {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices)
            }

            drawList.add(functionDraw)
        }
    }


    fun build() : GeneratedData{
        return GeneratedData(vertices, drawList)
    }
}