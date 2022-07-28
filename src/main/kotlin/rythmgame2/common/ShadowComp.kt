package rythmgame2.common

import ecs.Component
import ecs.ComponentKey
import engine.application.rendering.FrameBuffer
import engine.application.rendering.Mesh
import engine.application.rendering.Shader

class ShadowComp(
    val mesh: Mesh,
    val shadowBuffer: FrameBuffer,
    val shadowShader: Shader,
) : Component<ShadowComp> {
    override val key = ShadowComp

    companion object : ComponentKey<ShadowComp> {
        //TODO: Support for other mesh types than just Triangles, StaticDraw
        fun getShadowComp(
            mesh: Mesh,
            shadowBuffer: FrameBuffer,
            shadowShader: Shader
        ) = ShadowComp(
            Mesh(
                mesh.PrimitiveType.also { require(it == Mesh.Triangles) },
                mesh.RenderType.also { require(it == Mesh.StaticDraw) },
                mesh.Indices!!.toList()
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
                    .toIntArray(),
                *mesh.VertexData
            ),
            shadowBuffer,
            shadowShader,
        )
    }
}