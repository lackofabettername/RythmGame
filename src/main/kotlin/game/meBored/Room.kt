package game.meBored

import engine.application.Window
import engine.application.rendering.FrameBuffer
import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import engine.files.FileAccessMode
import engine.files.FileSystem
import logging.Log
import util.GeometryMath.lineIntersection
import util.Vector2
import util.Vector3

typealias Edge = Pair<Vector2, Vector2>

class Room(
    window: Window
) {

    val wallMesh: Mesh
    val vertices = ArrayList<Vector2>()

    val rayMesh: Mesh
    val edges = ArrayList<Edge>()
    val rays: ArrayList<Vector2>

    val shadowMesh: Mesh

    val shadowBuffer: FrameBuffer

    init {
        val indices = ArrayList<Int>()
        fun addEdge(a: Int, b: Int) {
            val edge = Edge(vertices[a], vertices[b])

            if (edge !in edges)
                edges += edge
            else
                edges -= edge
        }

        FileSystem.openResource("Walls.txt", FileAccessMode.Read)?.use {
            it.Reader.forEachLine { line ->
                if (line.isEmpty()) return@forEachLine

                val data = line.replace(Regex(".+\\(|\\)"), "")
                when (line.first()) {
                    'v' -> {
                        val (x, y) = data.split(Regex("\\s+"))
                        Log.debug("Vertex ($x, $y)")
                        vertices += Vector2(x.toFloat(), y.toFloat())
                    }
                    't' -> {
                        val (a, b, c) = data.split(Regex("\\s+")).map { it.toInt() }
                        Log.debug("Triangle ($a, $b, $c)")
                        indices += a
                        indices += b
                        indices += c

                        addEdge(a, b)
                        addEdge(b, c)
                        addEdge(c, a)
                    }
                }

            }
        }
        FileSystem.closeFile("Walls.txt")


        wallMesh = Mesh(
            Mesh.Triangles,
            Mesh.StaticDraw,
            indices.toIntArray(),
            //intArrayOf(0, 1, 2, 1, 2, 3),
            vertices.toTypedArray(),
            Array(vertices.size) { Vector3(0f, 0.5f, 1f) }
        )

        rays = ArrayList(vertices)
        rayMesh = Mesh(
            Mesh.Lines,
            Mesh.DynamicDraw,
            (vertices.indices).flatMapIndexed { i, _ -> listOf(i + 1, 0) }.toIntArray(),
            (vertices + Vector2(60f, 20f)).reversed().toTypedArray(),
            Array(vertices.size + 1) { Vector3(1f, 0f, 0.5f) }
        )

        shadowMesh = Mesh(
            Mesh.Triangles,
            Mesh.StaticDraw,
            indices.indices.step(3)
                .flatMap { i ->
                    (0..2).flatMap { j ->
                        listOf(
                            vertices[indices[i + (0 + j) % 3]],
                            vertices[indices[i + (0 + j) % 3]],
                            vertices[indices[i + (1 + j) % 3]],
                            vertices[indices[i + (0 + j) % 3]],
                            vertices[indices[i + (2 + j) % 3]],
                            vertices[indices[i + (1 + j) % 3]],
                        )
                    }
                }
                .toTypedArray(),
            Array(indices.size * 6) { Vector3(0f, 0f, 0f) }
        )


        shadowBuffer = FrameBuffer(window.Width, window.Height)
        shadowBuffer.attachColorBuffer()
        shadowBuffer.attachDepthBuffer()
        shadowBuffer.verify()
    }

    fun castRays(origin: Vector2) {
        rayMesh.updateVertex(0, origin, Vector3(1f, 1f, 1f))

        for (i in rays.indices) {
            val target = vertices[i].copy()
            target -= origin
            target.magnitude = 1000f
            target += origin

            val intersections = ArrayList<Vector2>()
            for (edge in edges) {
                val intersection = lineIntersection(origin, target, edge.first, edge.second) ?: continue

                intersections += intersection
            }


            if (intersections.isNotEmpty()) {
                intersections.sortBy { it.distSqr(origin) }
                rayMesh.updateVertex(i + 1, intersections[0], Vector3(1f, 0f, 0.5f))
            } else {
                rayMesh.updateVertex(i + 1, target, Vector3(1f, 0f, 0.5f))
            }
        }
    }

    fun render(shader: Shader, shadowShader: Shader) {
        //shadowBuffer.bind()
        //shadowBuffer.setClearColor(1f, 1f, 1f, 1f)
        //shadowBuffer.clear(FrameBuffer.ColorBuffer)

        shadowShader.bind()
        shadowShader.setUniform("castLength", 100f)
        shadowShader.setUniform("col", Vector3(0f, 1f, 0f))
        shadowMesh.render(shadowShader)

        shadowShader.setUniform("castLength", 1f)
        shadowShader.setUniform("col", Vector3(1f, 0f, 0f))
        shadowMesh.render(shadowShader)

        shadowBuffer.unbind()

        //GL11C.glBindTexture(GL_TEXTURE_2D, shadowBuffer.colorTexture)
        //wallMesh.render(shader)
        //rayMesh.render(shader)
    }
}