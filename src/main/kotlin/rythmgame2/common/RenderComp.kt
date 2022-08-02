package rythmgame2.common

import ecs.Component
import ecs.ComponentKey
import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import engine.application.rendering.Texture

open class RenderComp(
    val Mesh: Mesh,
    val Depth: Int,
    val Shader: Shader,
    val Texture: Texture?,
) : Component<RenderComp> {
    companion object : ComponentKey<RenderComp>

    override val key = RenderComp
}