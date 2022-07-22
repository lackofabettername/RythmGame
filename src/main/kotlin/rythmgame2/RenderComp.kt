package rythmgame2

import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import engine.application.rendering.Texture
import ecs.Component
import ecs.ComponentKey

class RenderComp(
    val Mesh: Mesh,
    val Texture: Texture,
    val Shader: Shader,
) : Component<RenderComp> {
    companion object : ComponentKey<RenderComp>

    override val key = RenderComp
}