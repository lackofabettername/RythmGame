package rythmGame

import engine.application.RenderInfo
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.InputEvent
import engine.application.rendering.Shader
import util.Matrix3x3

class Application : RenderLogic {
    lateinit var window: Window

    lateinit var shader: Shader
    val viewMat = Matrix3x3()

    lateinit var player: Player

    override fun onStart(renderInfo: RenderInfo) {
        window = renderInfo.Window

        shader = Shader()
        shader.createVertexShader("Test")
        shader.createFragmentShader("Test")
        shader.link()

        shader.createUniforms("viewTransform", "worldTransform", "lightTexture")

        player = Player()
    }

    override fun onUpdate() {

    }

    override fun onRender() {
        if (window.IsResized) {
            val v = (window.AspectRatio)
            viewMat.set(
                1f / 100 / v, 0f, 0f,
                0f, 1f / 100, 0f,
                0f, 0f, 1f,
            )

            shader.bind()
            shader.setUniform("viewTransform", viewMat)
        }

        window.clear(Window.ColorBuffer)

        shader.setUniform("viewTransform", viewMat)
        shader.setUniform("worldTransform", Matrix3x3.identity())
        player.mesh.render(shader)
    }

    override fun onClose() {

    }

    override fun onInputEvent(event: InputEvent) {

    }
}