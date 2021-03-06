package misccelaneous.meBored

import Color
import ColorMode
import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.*
import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import engine.console.ConsoleCommand
import misccelaneous.DummyClientLogic
import util.Matrix3x3
import util.Vector2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MeBored(
    val client: DummyClientLogic
) : RenderLogic {
    lateinit var engine: Engine
    lateinit var window: Window

    val viewMat = Matrix3x3()
    lateinit var shader: Shader
    lateinit var shadowShader: Shader

    val worldMat = Matrix3x3.identity()
    lateinit var lightSource: Mesh
    lateinit var room: Room

    val lightPos = Vector2()


    override fun initialize(window: Window) {
        this.window = window
        window.DepthTest = false
        window.CullFace = false
        //window.Blend = true
        //glBlendFunc(GL_SRC_ALPHA, GL_SRC_COLOR);

        shader = Shader()
        shader.createVertexShader("Test")
        shader.createFragmentShader("Test")
        shader.link()

        shader.createUniforms("viewTransform", "worldTransform", "lightTexture")

        shadowShader = Shader()
        shadowShader.createVertexShader("Shadows")
        shadowShader.createFragmentShader("Shadows")
        shadowShader.link()

        shadowShader.createUniforms("viewTransform", "worldTransform", "col", "castLength", "lightPos")


        lightSource = Mesh(
            Mesh.TriangleFan,
            Mesh.StaticDraw,
            (1..100).map { angle ->
                Vector2.fromAngle(angle / 50f * PI.toFloat()) * 4
            }.toTypedArray(),
            Array(100) {
                Color(ColorMode.RGB, 1f, 1f, 0.9f)
            }
        )

        room = Room(window)
    }

    override fun onStart(engine: Engine) {
        this.engine = engine
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

            shadowShader.bind()
            shadowShader.setUniform("viewTransform", viewMat)
        }

        window.setClearColor(0f, 0f, 0f, 1f)
        window.clear(Window.ColorBuffer)

        shader.bind()
        val t = (System.currentTimeMillis() % 6000) * PI.toFloat() * 2 / 6000
        lightPos.set(cos(t) * 50, sin(t) * 50)
        worldMat[2, 0] = lightPos.x
        worldMat[2, 1] = lightPos.y
        shader.setUniform("worldTransform", worldMat)
        lightSource.render(shader)

        worldMat[2, 0] = 0f
        worldMat[2, 1] = 0f
        shader.setUniform("worldTransform", worldMat)

        shadowShader.bind()
        shadowShader.setUniform("worldTransform", worldMat)
        shadowShader.setUniform("lightPos", lightPos)

        room.castRays(lightPos)
        room.render(shader, shadowShader)
    }

    override fun onClose() {
    }

    override fun onInputEvent(event: InputEvent) {
        if (event.EventType == InputEventType.Key) {
            if ((event as KeyEvent).Key == Key.Escape)
                engine.Application!!.enqueueEvent(ApplicationEvent(ConsoleCommand("exit", "")))
            else {
                //room = Room()
            }
        }
    }


}