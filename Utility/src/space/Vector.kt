package space

import java.io.Serializable

interface Vector : Serializable {
    fun getDimension(): Int
    operator fun get(axis: Int): Float
}