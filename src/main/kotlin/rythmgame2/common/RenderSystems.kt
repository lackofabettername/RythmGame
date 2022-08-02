package rythmgame2.common

import Color
import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType
import engine.application.rendering.FrameBuffer
import engine.console.logging.Log
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL30C
import rythmgame2.player.PlayerComp
import shaders.ColorShader
import shaders.ShadowsShader
import shaders.TextureShader
import java.lang.System.currentTimeMillis

object RenderSys : System {
    override val type = SystemType.Render
    override val keys = setOf(TransformComp, RenderComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val render = ecs[entity, RenderComp]
        val transform = ecs[entity, TransformComp]

        val shader = render.Shader
        shader.bind()

        if (render.Texture == null) {
            shader.uniforms[TextureShader.worldTransform] = transform.WorldTransform
            shader.uniforms[TextureShader.depth] = 1 - 1f / render.Depth
        } else {
            glActiveTexture(GL_TEXTURE0)
            render.Texture.bind()

            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)

            shader.uniforms[ColorShader.worldTransform] = transform.WorldTransform
            shader.uniforms[ColorShader.depth] = 1 - 1f / render.Depth
        }

        render.Mesh.render(shader)
    }
}

object ShadowSysPre : System {
    override val type = SystemType.Render
    override val keys = setOf(ShadowComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val shadow = ecs.Singleton[ShadowComp]

        Log.debug("Shadow pre", "hello")
    }
}

object ShadowSys : System {
    override val type = SystemType.Render
    override val keys = setOf(TransformComp, ShadowMeshComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val transform = ecs[entity, TransformComp]
        val mesh = ecs[entity, ShadowMeshComp].Mesh

        val lights = ecs[TransformComp, PlayerComp]
        val shadow = ecs.Singleton[ShadowComp]

        val buffer = shadow.Buffer
        buffer.bind()
        buffer.setClearColor(0.0f, 0f, 0f, 1f)
        buffer.clear(FrameBuffer.ColorBuffer or FrameBuffer.DepthBuffer)

        val shader = shadow.Shader
        shader.bind()
        shader.uniforms[ShadowsShader.worldTransform] = transform.WorldTransform
        shader.uniforms[ShadowsShader.lightPos] = ecs[lights.first(), TransformComp].Pos
        shader.uniforms[ShadowsShader.time] = (currentTimeMillis()  % 1_000_000) / 1000f
        //shader.uniforms[ShadowsShader.castLength] = 300f
        shader.uniforms[ShadowsShader.col] = Color.rgb(1f, 1f, 1f)

        mesh.render(shader)

        buffer.colorTexture.bind()
        GL30C.glGenerateMipmap(GL11C.GL_TEXTURE_2D)

        buffer.unbind()
    }
}