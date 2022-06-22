package rythmGame.rendering

import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.*
import engine.application.rendering.Shader
import engine.network.client.ClientState
import rythmGame.common.PlayerInput
import rythmGame.simulation.ClientLogic
import util.Matrix3x3

class GameRenderLogic(
    val window: Window,
    val client: ClientLogic
) : RenderLogic {
    lateinit var shader: Shader
    val viewMat = Matrix3x3()

    lateinit var playerKeyBinds: HashMap<Key, PlayerInput>

    var song: String = ""

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
            Key.LShift to PlayerInput.Dash
        )
        client.gsNow.player.graphics = client.gsNow.player.Graphics()
    }

    override fun onStart(engine: Engine) {
        updateViewMatrix()
    }

    override fun onUpdate() {

    }

    override fun onRender() {
        if (client.client.State != ClientState.Connected && client.client.State != ClientState.Active) {
            return
        }

        if (window.IsResized)
            updateViewMatrix()

        window.setClearColor(.05f, .05f, .05f, 1f)
        window.clear(Window.ColorBuffer)

        shader.setUniform("viewTransform", viewMat)
        client.gsNow.player.graphics!!.render(shader)
    }

    private fun updateViewMatrix() {
        viewMat.set(
            2f / window.Height / window.AspectRatio, 0f, -1f,
            0f, 2f / window.Height, -1f,
            0f, 0f, 1f,
        )

        shader.bind()
        shader.setUniform("viewTransform", viewMat)
    }

    override fun onClose() {

    }

    override fun onInputEvent(event: InputEvent) {
        if (event is KeyEvent) {
            client.playerInput(
                playerKeyBinds[event.Key] ?: return,
                when (event.Type) {
                    KeyEventType.Pressed -> true
                    KeyEventType.Released -> false
                    else -> return
                }
            )
        } else {
            client.playerInput(event as MouseEvent)
        }
    }
}