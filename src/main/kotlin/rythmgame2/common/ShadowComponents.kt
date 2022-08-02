package rythmgame2.common

import ecs.Component
import ecs.ComponentKey
import engine.application.rendering.FrameBuffer
import engine.application.rendering.Mesh
import engine.application.rendering.Shader

class ShadowComp(
    val Buffer: FrameBuffer,
    val Shader: Shader,
) : Component<ShadowComp> {
    override val key = ShadowComp

    companion object : ComponentKey<ShadowComp>
}

class ShadowMeshComp(
    val Mesh: Mesh,
) : Component<ShadowMeshComp> {
    override val key = ShadowMeshComp

    companion object : ComponentKey<ShadowMeshComp> {
        //TODO: Support for other mesh types than just Triangles, StaticDraw
        fun getShadowMesh(mesh: Mesh): ShadowMeshComp {
            val shadowIndices = mesh.Indices!!.toList()
                .chunked(3)//For every three indices (1 triangle)
                .flatMap { indices ->
                    (0..2).flatMap { i ->
                        //Create 2 new shadow triangles per vertex in the source triangle
                        listOf(
                            indices[(0 + i) % 3],
                            indices[(0 + i) % 3],
                            indices[(1 + i) % 3],
                            indices[(0 + i) % 3],
                            indices[(2 + i) % 3],
                            indices[(1 + i) % 3],
                        )
                    }
                }
                .toIntArray()

            //Create new buffer with copies of vertices in different orders
            val vertexData = shadowIndices.map { i ->
                mesh.VertexData[0][i] //FIXME: dangerous assumption that array 0 are the positions
            }.toTypedArray()

            return ShadowMeshComp(
                Mesh(
                    mesh.PrimitiveType.also { require(it == Mesh.Triangles) },
                    mesh.RenderType.also { require(it == Mesh.StaticDraw) },
                    vertexData,
                ),
            )
        }
    }
}