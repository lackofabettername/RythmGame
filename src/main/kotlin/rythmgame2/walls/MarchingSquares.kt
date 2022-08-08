package rythmgame2.walls

import data.*
import data.Map2.Companion.map2
import space.Vector2

class MarchingSquares(
    val nodes: Map2<Float>,
    val threshold: Float
) {
    val squares = nodes.indices - 1

    val vertices = Array(2) { axis ->
        map2(nodes.indices - Coord2.axis[axis]) {
            getVertex(axis, it.coord)
        }
    }
    val edges = ArrayList<Pair<Vector2, Vector2>>()

    private fun squareIndex(square: Coord2): Int {
        if (square !in squares) return -1

        return (if (nodes[square + Coord2(0, 0)] > threshold) 1 else 0) or
                (if (nodes[square + Coord2(1, 0)] > threshold) 2 else 0) or
                (if (nodes[square + Coord2(0, 1)] > threshold) 4 else 0) or
                (if (nodes[square + Coord2(1, 1)] > threshold) 8 else 0)
    }

    private fun getVertex(axis: Int, square: Coord2): Vector2 {
        val a = square
        val b = square + Coord2.axis[axis]

        val t = if ((nodes[a] > threshold) == (nodes[b] > threshold))
            0.5f
        else
            (threshold - nodes[a]) / (nodes[b] - nodes[a])

        return Vector2.lerp(t, a.vector, b.vector)
    }

    fun updateNode(node: Coord2) {
        for (axis in 0..1) {
            for (offset in 0..1) {
                val square = node + Coord2.axis[axis] * offset
                vertices[axis][square] = getVertex(axis, square)
            }
        }
    }

    fun edges(square: Coord2): List<Pair<Vector2, Vector2>> {
        val index = squareIndex(square)
        val edges = edgeTable[index]

        return edges.map { vertIndex ->
            val axis = vertIndex / 2
            val offset = Coord2.axis[1 - axis] * (vertIndex % 2)

            vertices[axis][offset + square]
        }.chunked(2).map { it[0] to it[1] }
    }

    fun updateEdges() {
        edges.clear()
        for (square in squares) {
            edges += edges(square)
        }
    }

    companion object {
        val edgeTable = arrayOf(
            emptyArray(),
            arrayOf(0, 2),
            arrayOf(0, 3),
            arrayOf(2, 3),

            arrayOf(1, 2),
            arrayOf(0, 1),
            arrayOf(0, 3, 1, 2),
            arrayOf(1, 3),

            arrayOf(1, 3),
            arrayOf(0, 2, 1, 3),
            arrayOf(0, 1),
            arrayOf(1, 2),

            arrayOf(2, 3),
            arrayOf(0, 3),
            arrayOf(0, 2),
            emptyArray()
        )
    }
}