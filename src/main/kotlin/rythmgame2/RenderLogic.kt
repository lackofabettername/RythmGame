package rythmgame2

import data.Color
import data.Map2.Companion.map2
import ecs.ECS
import ecs.SystemType
import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.*
import engine.application.rendering.FrameBuffer
import engine.application.rendering.Mesh
import org.lwjgl.opengl.GL11.*
import rythmgame2.common.*
import rythmgame2.player.PlayerComp.Companion.createPlayer
import rythmgame2.player.PlayerSys
import space.Matrix3x3
import space.Vector
import space.Vector2

class RenderLogic : RenderLogic {
    lateinit var window: Window

    val shadowDownscale = 6

    val ecs = ECS()
    val input = InputComp()

    val shaders by lazy { ShaderComp(viewMatrix, shadowDownscale) }

    val shadowBuffer by lazy {
        val buffer = FrameBuffer(window.Width / shadowDownscale, window.Height / shadowDownscale)
        buffer.attachColorBuffer()
        buffer.attachDepthBuffer()
        buffer.verify()
        buffer
    }
    val shadows by lazy { ShadowBufferComp(shadowBuffer) }

    val player by lazy { createPlayer(ecs) }
    val walls by lazy { createWalls(ecs) }
    val temp = ecs.createEntity()

    override fun initialize(window: Window) {
        this.window = window

        window.Blend = true
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        window.DepthTest = true

        ecs.Singleton += input
        ecs.Singleton += shaders
        ecs.Singleton += shadows

        ecs.Systems += LifeSys

        ecs.Systems += PlayerSys
        ecs.Systems += MoveSys

        ecs.Systems += SpeedSmearSys

        ecs.Systems += ShadowSysPre
        ecs.Systems += ShadowSys

        ecs.Systems += RenderSysPre
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
            4,
            shadowBuffer.colorTexture
        )
    }

    private val viewMatrix
        get() = Matrix3x3(
            2f / window.Width, 0f, -1f,
            0f, 2f / window.Height, -1f,
            0f, 0f, 1f
        )

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