package rythmgame2.walls

import data.*
import data.Map2.Companion.map2
import misc.then
import space.Vector2
import kotlin.math.sign

private typealias Vertex = Pair<Int, Coord2>

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
    val triangles = ArrayList<Triple<Vector2, Vector2, Vector2>>()

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

    fun edges(square: Coord2): List<Vertex> {
        val index = squareIndex(square)
        val edges = edgeTable[index]

        return edges.map { vertIndex ->
            val axis = vertIndex / 2
            val offset = Coord2.axis[1 - axis] * (vertIndex % 2)

            axis to square + offset
            //vertices[axis][offset + square]
        }
    }

    fun triangles(square: Coord2): List<Triple<Vector2, Vector2, Vector2>> {
        val index = squareIndex(square)
        val triangles = triangleTable[index]

        return triangles.map { vertIndex ->
            if (vertIndex < 4) {
                val axis = vertIndex / 2 % 2
                val offset = Coord2.axis[1 - axis] * (vertIndex % 2)

                vertices[axis][square + offset]
            } else {
                val offset = Coord2.axis[0] * vertIndex % 2 +
                        Coord2.axis[1] * (vertIndex / 2 % 2)

                (square + offset).vector
            }
        }.chunked(3).map { it[0] to it[1] then it[2] }
    }

    fun updateTriangles() {
        triangles.clear()
        for (square in squares) {
            triangles += triangles(square)
        }
    }

    fun getMesh() {
        //Collect all edges
        val edges = ArrayDeque<Vertex>()
        for (square in squares) {
            edges += edges(square)
        }

        //Group edges into loops
        val loops = ArrayList<ArrayList<Vertex>>()

        var nextInd = 0
        var loop = ArrayList<Vertex>()
        while (edges.isNotEmpty()) {
            edges.removeAt(nextInd)
            val current = edges.removeAt((nextInd / 2) * 2)

            loop += current

            nextInd = edges.indexOf(current)

            if (nextInd == -1) {
                loops += loop
                loop = ArrayList()
                nextInd = 0
            }
        }

        //Splice loops into convex hulls
        val hulls = ArrayList<ArrayDeque<Vector2>>()

        while (loops.isNotEmpty()) {
            loop = loops.removeFirst()

            while (loop.isNotEmpty()) {
                if (loop.size < 2) TODO()

                val hull = ArrayDeque<Vector2>()
                repeat(2) {
                    hull.addFirst(loop.removeFirst()
                        .let { (axis, square) -> vertices[axis][square] })
                }

                var i = 0
                while (i < loop.size) {
                    hull.addFirst(loop[i++].let { (axis, square) -> vertices[axis][square] })

                    val (c, b, a) = hull
                    val sign = ((b - a).perpendicular dot c).sign

                    if (sign == -1f) {
                        //Split
                        hull.removeFirst()
                    } else {
                        loop.removeAt(--i)
                    }

                    hulls += hull
                }
            }
        }

        //Use convex hulls to generate triangles

        //Combine into Mesh
    }

    companion object {
        val edgeTable = arrayOf(
            emptyArray(),
            arrayOf(2, 0),
            arrayOf(0, 3),
            arrayOf(2, 3),

            arrayOf(1, 2),
            arrayOf(1, 0),
            arrayOf(0, 3, 1, 2),
            arrayOf(1, 3),

            arrayOf(3, 1),
            arrayOf(2, 0, 3, 1),
            arrayOf(0, 1),
            arrayOf(1, 2),

            arrayOf(3, 2),
            arrayOf(3, 0),
            arrayOf(0, 2),
            emptyArray()
        )

        @Deprecated("")
        val triangleTable = arrayOf(
            emptyArray(),
            arrayOf(0, 2, 4),
            arrayOf(0, 3, 5),
            arrayOf(2, 3, 5, 4, 5, 2),

            arrayOf(1, 2, 6),
            arrayOf(0, 1, 4, 4, 6, 1),
            arrayOf(0, 3, 5, 1, 2, 6),
            arrayOf(1, 3, 4, 4, 5, 3, 4, 6, 1),

            arrayOf(1, 3, 7),
            arrayOf(0, 2, 4, 1, 3, 7),
            arrayOf(0, 1, 5, 5, 7, 1),
            arrayOf(1, 2, 5, 4, 5, 2, 5, 7, 1),

            arrayOf(2, 3, 6, 6, 7, 3),
            arrayOf(0, 3, 6, 4, 6, 0, 6, 7, 3),
            arrayOf(0, 2, 7, 5, 7, 0, 2, 6, 7),
            arrayOf(4, 5, 6, 5, 6, 7)
        )
    }
}