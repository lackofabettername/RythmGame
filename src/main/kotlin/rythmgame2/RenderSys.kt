package rythmgame2

import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType

object RenderSys : System {
    override val type = SystemType.Render
    override val keys = setOf(TransformComp, RenderComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val render = ecs[entity, RenderComp]
        val transform = ecs[entity, TransformComp]

        val shader = render.Shader
        shader.bind()
        shader.uniforms["worldTransform"] = transform.WorldTransform


        glActiveTexture(GL_TEXTURE0)
        render.Texture.bind()

        render.Mesh.render(shader)
    }
}