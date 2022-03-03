package misccelaneous

import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.*
import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import engine.console.ConsoleCommand
import engine.console.logging.Log
import rythmGame.rendering.GUI
import util.Matrix3x3
import util.Vector2
import util.Vector3
import kotlin.math.PI
import kotlin.math.sin

class DummyRenderLogic(
    val client: DummyClientLogic
) : RenderLogic {
    lateinit var engine: Engine
    lateinit var window: Window

    val gui = GUI()

    lateinit var shader: Shader
    lateinit var mesh: Mesh

    val viewMat = Matrix3x3()
    val worldMat = Matrix3x3.identity()
    val playerPos = Vector2()

    var frameCount = 0

    override fun initialize(window: Window) {
        this.window = window

        gui.initialize(window)
        gui.Windows += debugGUI(this)

        shader = Shader()
        shader.createVertexShader("Test")
        shader.createFragmentShader("Test")
        shader.link()

        shader.bind()
        shader.createUniforms("viewTransform", "worldTransform")

        mesh = Mesh(
            Mesh.Triangles,
            Mesh.StaticDraw,
            arrayOf(
                Vector3(Vector2.fromAngle(PI.toFloat() * 0.0f, 20f), 0f),
                Vector3(Vector2.fromAngle(PI.toFloat() / 1.5f, 20f), 0f),
                Vector3(Vector2.fromAngle(PI.toFloat() / .75f, 20f), 0f),
            ),
            arrayOf(
                Vector3(1f, 1f, 1f),
                Vector3(1f, 1f, 0f),
                Vector3(1f, 0f, 1f),
            )
        )
    }

    override fun onStart(engine: Engine) {
        this.engine = engine
    }

    override fun onUpdate() {
    }

    override fun onRender() {
        if (window.IsResized) {
            shader.bind()
            val v = (window.AspectRatio)
            viewMat.set(
                1f / 100 / v, 0f, 0f,
                0f, 1f / 100, 0f,
                0f, 0f, 1f,
            )
            shader.setUniform("viewTransform", viewMat)
        }

        //Log.trace("RenderLogic", "frame #$frameCount")
        ++frameCount

        window.setClearColor(1f, playerPos.y, 0f, 0f)
        window.setClearColor(0.5f, 0.5f, 0.5f, 0f)
        window.clear(Window.ColorBuffer)

        shader.bind()
        val v = sin((System.currentTimeMillis() % 2000) * PI.toFloat() * 2 / 2000) / 2 + 0.5f
        worldMat[2, 0] = v
        shader.setUniform("worldTransform", worldMat)
        mesh.render(shader)

        gui.render()
    }

    override fun onClose() {
        gui.cleanup()
    }

    override fun onInputEvent(event: InputEvent) {
        Log.trace("RenderLogic", "Handling event $event")
        if (event.EventType == InputEventType.Key) {
            if ((event as KeyEvent).Type == KeyEventType.Pressed) {
                when (event.Key) {
                    Key.Escape -> {
                        engine.Application!!.enqueueEvent(
                            ApplicationEvent(ConsoleCommand("exit", ""))
                        )
                    }
                }
            }
        }
    }
}