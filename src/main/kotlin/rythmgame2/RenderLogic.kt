package rythmgame2

import ecs.ECS
import ecs.SystemType
import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.*
import engine.application.rendering.FrameBuffer
import engine.application.rendering.Shader
import rythmgame2.common.InputComp
import rythmgame2.common.InputSys
import rythmgame2.common.RenderSys
import rythmgame2.common.createWalls
import rythmgame2.player.PlayerComp.Companion.createPlayer
import rythmgame2.player.PlayerSys
import shaders.ColorShader
import shaders.TextureShader
import util.Matrix3x3

class RenderLogic : RenderLogic {
    lateinit var window: Window

    val ecs = ECS()
    val input = InputComp()

    lateinit var textureShader: Shader
    lateinit var colorShader: Shader

    lateinit var shadowBuffer: FrameBuffer

    val player by lazy { createPlayer(ecs, textureShader) }
    val walls by lazy { createWalls(ecs, shadowBuffer, colorShader) }

    override fun initialize(window: Window) {
        this.window = window

        ecs.Singleton += input

        ecs.Systems += RenderSys
        ecs.Systems += PlayerSys

        ecs.Systems += InputSys //Add this last!

        createShaders()

        shadowBuffer = FrameBuffer(window.Width, window.Height)

        //trigger lazy
        player.id
        walls.id
    }

    private val viewMatrix
        get() = Matrix3x3(
            2f / window.Width, 0f, -1f,
            0f, 2f / window.Height, -1f,
            0f, 0f, 1f
        )

    fun createShaders() {
        //region Texture shader
        textureShader = Shader()
        textureShader.createVertexShader(TextureShader.Path)
        textureShader.createFragmentShader(TextureShader.Path)
        textureShader.link()
        textureShader.bind()

        textureShader.uniforms += TextureShader.viewTransform
        textureShader.uniforms += TextureShader.worldTransform
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

        colorShader.uniforms[ColorShader.viewTransform] = viewMatrix
        //endregion
    }

    override fun onStart(engine: Engine) {
    }

    override fun onUpdate() {
        ecs.update(SystemType.Update)
    }

    override fun onRender() {
        window.clear(Window.ColorBuffer)
        //window.setClearColor(Random.nextFloat(), 0f, 0f, 1f)

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