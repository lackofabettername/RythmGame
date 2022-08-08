package rythmgame2.common

import ecs.Component
import ecs.ComponentKey
import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import engine.application.rendering.Texture
import engine.files.FileAccessMode
import engine.files.FileSystem
import shaders.ColorShader
import shaders.ShadowsShader
import shaders.TextureShader
import space.Matrix3x3

class RenderComp(
    val Mesh: Mesh,
    val Depth: Int,
    val color: Int,
    val Texture: Texture?,
) : Component<RenderComp> {
    companion object : ComponentKey<RenderComp>

    override val key = RenderComp
}

class ShaderComp(
    viewMatrix: Matrix3x3,
    shadowDownscale: Int
) : Component<ShaderComp> {
    val texture = Shader()
    val color = Shader()
    val shadow = Shader()
    val colorMap = FileSystem.openResource("Palette.png", FileAccessMode.Read)!!
        .use { Texture.load1D(it) }

    init {
        //region Texture shader
        texture.createVertexShader(TextureShader.Path)
        texture.createFragmentShader(TextureShader.Path)
        texture.link()
        texture.bind()

        texture.uniforms += TextureShader.viewTransform
        texture.uniforms += TextureShader.worldTransform
        texture.uniforms += TextureShader.depth
        texture.uniforms += TextureShader.color

        texture.uniforms += TextureShader.spriteTexture
        texture.uniforms += TextureShader.palette
        texture.uniforms[TextureShader.spriteTexture] = 0
        texture.uniforms[TextureShader.palette] = 1

        texture.uniforms[TextureShader.viewTransform] = viewMatrix
        //endregion

        //region Color shader
        color.createVertexShader(ColorShader.Path)
        color.createFragmentShader(ColorShader.Path)
        color.link()
        color.bind()

        color.uniforms += ColorShader.viewTransform
        color.uniforms += ColorShader.worldTransform
        color.uniforms += ColorShader.depth
        color.uniforms += ColorShader.color

        texture.uniforms += TextureShader.palette
        texture.uniforms[TextureShader.palette] = 1

        color.uniforms[ColorShader.viewTransform] = viewMatrix
        //endregion

        //region Shadow shader
        shadow.createVertexShader(ShadowsShader.Path)
        shadow.createFragmentShader(ShadowsShader.Path)
        shadow.link()
        shadow.bind()

        shadow.uniforms += ShadowsShader.viewTransform
        shadow.uniforms += ShadowsShader.worldTransform

        shadow.uniforms += ShadowsShader.lightPos
        shadow.uniforms += ShadowsShader.col
        shadow.uniforms += ShadowsShader.pass

        shadow.uniforms += ShadowsShader.time

        shadow.uniforms[ShadowsShader.viewTransform] = viewMatrix * Matrix3x3(
            1f / shadowDownscale, 0f, 0f,
            0f, 1f / shadowDownscale, 0f,
            0f, 0f, 1f
        )
        //endregion
    }

    companion object : ComponentKey<ShaderComp>

    override val key = ShaderComp
}