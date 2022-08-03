package rythmgame2.common

import Color
import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType
import engine.application.Window
import engine.application.rendering.FrameBuffer
import rythmgame2.player.PlayerComp
import shaders.ColorShader
import shaders.ShadowsShader
import shaders.TextureShader
import java.lang.System.currentTimeMillis

object RenderSysPre : System {
    override val type = SystemType.Render
    override val keys = setOf(ShaderComp) //todo: use other component?

    override fun invoke(ecs: ECS, entity: Entity) {
        Window.bind()
    }
}

object RenderSys : System {
    override val type = SystemType.Render
    override val keys = setOf(TransformComp, RenderComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val render = ecs[entity, RenderComp]
        val transform = ecs[entity, TransformComp]

        val shader = if (render.Texture == null) {
            ecs.Singleton[ShaderComp].color
        } else {
            ecs.Singleton[ShaderComp].texture
        }

        shader.bind()

        if (render.Texture == null) {
            shader.uniforms[ColorShader.worldTransform] = transform.WorldTransform
            shader.uniforms[ColorShader.depth] = 1 - 1f / render.Depth
            shader.uniforms[ColorShader.color] = render.color
        } else {
            render.Texture.bind(0)
            ecs.Singleton[ShaderComp].colorMap.bind(1)

            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)

            shader.uniforms[TextureShader.worldTransform] = transform.WorldTransform
            shader.uniforms[TextureShader.depth] = 1 - 1f / render.Depth
            shader.uniforms[TextureShader.color] = render.color
        }

        render.Mesh.render(shader)
    }
}

object ShadowSysPre : System {
    override val type = SystemType.Render
    override val keys = setOf(ShadowComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val buffer = ecs.Singleton[ShadowComp].Buffer
        buffer.bind()
        buffer.setClearColor(0.0f, 0f, 0f, 1f)
        buffer.clear(FrameBuffer.ColorBuffer or FrameBuffer.DepthBuffer)

        val shader = ecs.Singleton[ShaderComp].shadow
        shader.bind()
        shader.uniforms[ShadowsShader.time] = (currentTimeMillis() % 1_000_000) / 1000f
    }
}

object ShadowSys : System {
    override val type = SystemType.Render
    override val keys = setOf(TransformComp, ShadowMeshComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val transform = ecs[entity, TransformComp]
        val mesh = ecs[entity, ShadowMeshComp].Mesh

        val lights = ecs[TransformComp, PlayerComp]

        val shader = ecs.Singleton[ShaderComp].shadow
        shader.uniforms[ShadowsShader.worldTransform] = transform.WorldTransform
        shader.uniforms[ShadowsShader.lightPos] = ecs[lights.first(), TransformComp].Pos
        //shader.uniforms[ShadowsShader.castLength] = 300f
        shader.uniforms[ShadowsShader.col] = Color.rgb(1f, 1f, 1f)

        mesh.render(shader)
    }
}