package rythmgame2

import Color
import ecs.ECS
import ecs.SystemType
import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.*
import engine.application.rendering.FrameBuffer
import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import org.lwjgl.opengl.GL11.*
import rythmgame2.common.*
import rythmgame2.player.PlayerComp.Companion.createPlayer
import rythmgame2.player.PlayerSys
import shaders.ColorShader
import shaders.ShadowsShader
import shaders.TextureShader
import util.Matrix3x3
import util.Vector
import util.Vector2

class RenderLogic : RenderLogic {
    lateinit var window: Window

    val ecs = ECS()
    val input = InputComp()
    val shadows by lazy { ShadowComp(shadowBuffer, shadowShader) }

    lateinit var textureShader: Shader
    lateinit var colorShader: Shader
    lateinit var shadowShader: Shader

    val shadowDownScale = 6
    val shadowBuffer by lazy {
        val buffer = FrameBuffer(window.Width / shadowDownScale, window.Height / shadowDownScale)
        buffer.attachColorBuffer()
        buffer.attachDepthBuffer()
        buffer.verify()
        buffer
    }

    val player by lazy { createPlayer(ecs, textureShader) }
    val walls by lazy { createWalls(ecs, colorShader) }
    val temp = ecs.createEntity()

    override fun initialize(window: Window) {
        this.window = window

        window.Blend = true
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        window.DepthTest = true

        createShaders()


        ecs.Singleton += input
        ecs.Singleton += shadows

        ecs.Systems += PlayerSys

        ecs.Systems += ShadowSysPre
        ecs.Systems += ShadowSys

        ecs.Systems += RenderSys

        ecs.Systems += InputSys //Add this last!

        //trigger lazy
        walls.id
        player.id

        ecs[temp] += TransformComp()
        ecs[temp] += RenderComp(
            Mesh(
                Mesh.TriangleStrip,
                Mesh.StaticDraw,
                Vector2.arrayOf(
                    0f, 0f,
                    900f, 0f,
                    0f, 600f,
                    900f, 600f
                ) as Array<Vector>,
                Vector2.arrayOf(
                    0f, 0f,
                    1f, 0f,
                    0f, 1f,
                    1f, 1f
                ) as Array<Vector>,
            ),
            20,
            textureShader,
            shadowBuffer.colorTexture
        )
    }

    private val viewMatrix
        get() = Matrix3x3(
            2f / window.Width, 0f, -1f,
            0f, 2f / window.Height, -1f,
            0f, 0f, 1f
        )

    private fun createShaders() {
        //region Texture shader
        textureShader = Shader()
        textureShader.createVertexShader(TextureShader.Path)
        textureShader.createFragmentShader(TextureShader.Path)
        textureShader.link()
        textureShader.bind()

        textureShader.uniforms += TextureShader.viewTransform
        textureShader.uniforms += TextureShader.worldTransform
        textureShader.uniforms += TextureShader.depth
        textureShader.uniforms += TextureShader.spriteTexture

        textureShader.uniforms[TextureShader.viewTransform] = viewMatrix
        //endregion

        //region Color shader
        colorShader = Shader()
        colorShader.createVertexShader(ColorShader.Path)
        colorShader.createFragmentShader(ColorShader.Path)
        colorShader.link()
        colorShader.bind()

        colorShader.uniforms += ColorShader.viewTransform
        colorShader.uniforms += ColorShader.worldTransform
        colorShader.uniforms += ColorShader.depth

        colorShader.uniforms[ColorShader.viewTransform] = viewMatrix
        //endregion

        //region Shadow shader
        shadowShader = Shader()
        shadowShader.createVertexShader(ShadowsShader.Path)
        shadowShader.createFragmentShader(ShadowsShader.Path)
        shadowShader.link()
        shadowShader.bind()

        shadowShader.uniforms += ShadowsShader.viewTransform
        shadowShader.uniforms += ShadowsShader.worldTransform

        //shadowShader.uniforms += ShadowsShader.castLength
        shadowShader.uniforms += ShadowsShader.lightPos
        shadowShader.uniforms += ShadowsShader.col
        shadowShader.uniforms += ShadowsShader.time

        shadowShader.uniforms[ShadowsShader.viewTransform] = viewMatrix * Matrix3x3(
            1f / shadowDownScale, 0f, 0f,
            0f, 1f / shadowDownScale, 0f,
            0f, 0f, 1f
        )
        //endregion
    }

    override fun onStart(engine: Engine) {
    }

    override fun onUpdate() {
        ecs.update(SystemType.Update)
    }

    override fun onRender() {
        window.clearColor = Color.rgb(0f, 0f, 0f)
        window.clear(Window.ColorBuffer or Window.DepthBuffer)

        ecs.update(SystemType.Render)
    }

    override fun onClose() {
    }

    override fun onInputEvent(event: InputEvent) {
        when (event.EventType) {
            InputEventType.Key -> {
                val event = event as KeyEvent
                when (event.Type) {
                    KeyEventType.Pressed -> {
                        input.PressedKeys += event.Key
                        input.HeldKeys += event.Key
                    }

                    KeyEventType.Released -> {
                        input.ReleasedKeys += event.Key
                        input.HeldKeys -= event.Key
                    }
                }
            }

            InputEventType.Mouse -> {
                val event = event as MouseEvent
                when (event.Type) {
                    MouseEventType.ButtonPressed -> {
                        input.PressedButtons += event.Button
                        input.HeldButtons += event.Button
                    }

                    MouseEventType.ButtonReleased -> {
                        input.ReleasedButtons += event.Button
                        input.HeldButtons -= event.Button
                    }

                    MouseEventType.Moved -> {
                        input.Mouse copyFrom event.Position
                    }

                    MouseEventType.Wheel -> {
                        input.scroll copyFrom event.Wheel
                    }
                }
            }
        }
    }
}