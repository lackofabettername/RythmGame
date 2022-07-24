package rythmgame2

import ecs.ECS
import ecs.Entity
import ecs.SystemType
import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.*
import engine.application.rendering.Shader
import rythmgame2.common.InputComp
import rythmgame2.common.InputSys
import rythmgame2.common.RenderSys
import shaders.TestShader
import util.Matrix3x3

class RenderLogic : RenderLogic {
    lateinit var window: Window

    val ecs = ECS()
    val input = InputComp()

    var player = Entity(-1, -1)

    lateinit var shader: Shader

    override fun initialize(window: Window) {
        this.window = window

        ecs.Singleton += input

        ecs.Systems += RenderSys
        ecs.Systems += PlayerSys

        ecs.Systems += InputSys //Add this last!


        createShader()
        player = PlayerComp.createPlayer(ecs, shader)
    }

    fun createShader() {
        shader = Shader()
        shader.createVertexShader(TestShader.Path)
        shader.createFragmentShader(TestShader.Path)
        shader.link()
        shader.bind()

        shader.uniforms += TestShader.viewTransform
        shader.uniforms += TestShader.worldTransform
        shader.uniforms += TestShader.spriteTexture

        shader.uniforms[TestShader.viewTransform] = Matrix3x3(
            2f / window.Width, 0f, -1f,
            0f, 2f / window.Height, -1f,
            0f, 0f, 1f
        )

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