package space

import java.io.Serializable

interface Vector : Serializable {
    val Dimension: Int
    operator fun get(axis: Int): Float
}