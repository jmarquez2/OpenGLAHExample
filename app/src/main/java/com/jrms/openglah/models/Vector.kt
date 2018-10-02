package com.jrms.openglah.models

class Vector(val x : Float, val y : Float, val z : Float) {

    fun crossProduct(vector: Vector): Vector {
        return Vector(
                (y * vector.z) - (z * vector.y),
                (z * vector.x) - (x * vector.z),
                (x * vector.y) - (y * vector.x)
        )
    }

    fun length(): Float {
        return Math.sqrt(Math.pow(x.toDouble(), 2.0) +
        Math.pow(y.toDouble(), 2.0) +
        Math.pow(z.toDouble(), 2.0)).toFloat()
    }

    fun dotProduct(vector: Vector): Float {
        return x * vector.x + y * vector.y + z * vector.z
    }

    fun scale(scaleFactor: Float): Vector {
        return Vector(x * scaleFactor, y * scaleFactor, z * scaleFactor)
    }
}