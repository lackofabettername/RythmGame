package rythmgame2.common

import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import shaders.TextureShader

object RenderSys : System {
    override val type = SystemType.Render
    override val keys = setOf(TransformComp, RenderComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val render = ecs[entity, RenderComp]
        val transform = ecs[entity, TransformComp]

        val shader = render.Shader
        shader.bind()
        shader.uniforms[TextureShader.worldTransform] = transform.WorldTransform

        render.Texture?.let {texture ->
            glActiveTexture(GL_TEXTURE0)
            texture.bind()
        }

        render.Mesh.render(shader)
    }
}