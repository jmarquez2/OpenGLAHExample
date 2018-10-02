package com.jrms.openglah.models.geometry

import com.jrms.openglah.models.Vector

class Point (val x : Float, val y : Float, val z : Float){

    fun translateY(distance : Float) : Point{
        return Point(x, y + distance, z)
    }

    fun translate(vector: Vector): Point {
        return Point(x + vector.x,
                y + vector.y ,
                z + vector.z)
    }
}