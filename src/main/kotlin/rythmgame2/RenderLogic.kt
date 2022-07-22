package rythmgame2

import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import engine.application.rendering.Texture
import engine.files.FileAccessMode
import engine.files.FileSystem
import util.Matrix3x3
import util.Vector
import util.Vector2
import ecs.ECS
import ecs.SystemType
import engine.application.events.*

class RenderLogic : RenderLogic {
    lateinit var window: Window

    val ecs = ECS()
    val input = InputComp()

    val player = ecs.createEntity()

    lateinit var shader: Shader

    override fun initialize(window: Window) {
        this.window = window

        ecs.Singleton += input

        ecs.Systems += RenderSys
        ecs.Systems += PlayerSys

        ecs.Systems += InputSys //Add this last!


        createShader()
        createPlayer()
    }

    fun createShader() {
        shader = Shader()
        shader.createVertexShader("Test")
        shader.createFragmentShader("Test")
        shader.link()
        shader.bind()

        shader.uniforms += "viewTransform"
        shader.uniforms += "worldTransform"
        shader.uniforms += "spriteTexture"

        shader.uniforms["viewTransform"] = Matrix3x3(
            2f / window.Width, 0f, -1f,
            0f, 2f / window.Height, -1f,
            0f, 0f, 1f
        )

    }

    fun createPlayer() {
        val mesh = Mesh(
            Mesh.Triangles,
            Mesh.StaticDraw,
            intArrayOf(0, 1, 2, 1, 2, 3),
            (Vector2.arrayOf(
                0f, 0f,
                64f, 0f,
                0f, 64f,
                64f, 64f
            )) as Array<Vector>,
            Vector2.arrayOf(
                0f, 0f,
                1f, 0f,
                0f, 1f,
                1f, 1f
            ) as Array<Vector>
        )

        val texture = FileSystem.openResource("Player.png", FileAccessMode.Read)!!
            .use { Texture(it) }

        ecs.addComponent(
            player,
            TransformComp(
                Vector2(450.0f, 300.0f),
                0.0f,
                Vector2(1f)
            ),
            RenderComp(
                mesh,
                texture,
                shader
            ),
            PlayerComp(
                hashMapOf(
                    Key.W to Vector2(0f, 1f),
                    Key.A to Vector2(-1f, 0f),
                    Key.S to Vector2(0f, -1f),
                    Key.D to Vector2(1f, 0f),
                )
            )
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