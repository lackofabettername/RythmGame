package data

import java.io.Serializable

interface Coord : Serializable {
    val Dimension: Int
    operator fun get(axis: Int): Int
}