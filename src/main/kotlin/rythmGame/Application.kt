package rythmGame

import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.InputEvent
import engine.application.events.Key
import engine.application.events.KeyEvent
import engine.application.events.KeyEventType
import engine.application.rendering.Shader
import util.Matrix3x3

class Application(
    val client: ClientLogic
) : RenderLogic {
    lateinit var window: Window

    lateinit var shader: Shader
    val viewMat = Matrix3x3()

    lateinit var playerKeyBinds: HashMap<Key, PlayerInput>

    override fun initialize(window: Window) {
        shader = Shader()
        shader.createVertexShader("Test")
        shader.createFragmentShader("Test")
        shader.link()

        shader.createUniforms("viewTransform", "worldTransform", "spriteTexture")

        playerKeyBinds = hashMapOf(
            Key.W to PlayerInput.MoveUp,
            Key.A to PlayerInput.MoveLeft,
            Key.S to PlayerInput.MoveDown,
            Key.D to PlayerInput.MoveRight,
        )
        client.player.graphics = client.player.Graphics()
    }

    override fun onStart(engine: Engine) {
        window = engine.Application!!.Window

    }

    override fun onUpdate() {

    }

    override fun onRender() {
        if (window.IsResized) {
            viewMat.set(
                1f / window.Height / window.AspectRatio, 0f, 0f,
                0f, 1f / window.Height, 0f,
                0f, 0f, 1f,
            )

            shader.bind()
            shader.setUniform("viewTransform", viewMat)
        }

        window.clear(Window.ColorBuffer)

        shader.setUniform("viewTransform", viewMat)
        client.player.graphics!!.render(shader)
    }

    override fun onClose() {

    }

    override fun onInputEvent(event: InputEvent) {
        if (event is KeyEvent) {
            playerKeyBinds[event.Key]?.let {
                client.playerInput(it, event.Type != KeyEventType.Released)
            }
        }
    }
}