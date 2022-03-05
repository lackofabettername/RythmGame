package rythmGame.rendering

import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.InputEvent
import engine.application.events.Key
import engine.application.events.KeyEvent
import engine.application.events.KeyEventType
import engine.application.rendering.Shader
import engine.console.logging.Log
import engine.network.client.ClientState
import engine.network.common.NetAddress
import rythmGame.simulation.ClientLogic
import rythmGame.common.PlayerInput
import util.Matrix3x3

class GameRenderLogic(
    val window: Window,
    val client: ClientLogic
) : RenderLogic {
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
        client.gsNow.player.graphics = client.gsNow.player.Graphics()
    }

    override fun onStart(engine: Engine) {
        Log.debug("Client", "Connecting to server")
        do {
            client.client.connect(NetAddress.loopbackServer)
            Thread.sleep(100)
        } while (client.client.State != ClientState.Connected && client.client.State != ClientState.Active)
    }

    override fun onUpdate() {

    }

    override fun onRender() {
        if (window.IsResized)
            updateViewMatrix()


        window.setClearColor(.05f, .05f, .05f, 1f)
        window.clear(Window.ColorBuffer)

        shader.setUniform("viewTransform", viewMat)
        client.gsNow.player.graphics!!.render(shader)
    }

    private fun updateViewMatrix() {
        viewMat.set(
            1f / window.Height / window.AspectRatio, 0f, 0f,
            0f, 1f / window.Height, 0f,
            0f, 0f, 1f,
        )

        shader.bind()
        shader.setUniform("viewTransform", viewMat)
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