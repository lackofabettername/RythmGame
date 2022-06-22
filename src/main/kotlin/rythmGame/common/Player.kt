package rythmGame.common

import engine.application.rendering.Mesh
import engine.application.rendering.Shader
import engine.application.rendering.Texture
import engine.files.FileAccessMode
import engine.files.FileSystem
import org.lwjgl.opengl.GL13C.GL_TEXTURE0
import org.lwjgl.opengl.GL13C.glActiveTexture
import util.Matrix3x3
import util.Vector
import util.Vector2
import util.times
import java.io.Serializable

class Player : Serializable {

    val inputs = HashSet<PlayerInput>()
    val mouse = Vector2()
    val pos = Vector2()
    val vel = Vector2()

    fun update(t: Float) {
        val acc = Vector2()

        if (PlayerInput.Dash in inputs) {
            acc += (mouse - pos)
            acc.magnitude = vel.magnitude + 2f

            vel *= 0
            inputs -= PlayerInput.Dash
        } else {
            for (input in inputs) {
                when (input) {
                    //@formatter:off
                    PlayerInput.MoveLeft  -> acc.x -= 1f
                    PlayerInput.MoveRight -> acc.x += 1f

                    PlayerInput.MoveUp    -> acc.y += 1f
                    PlayerInput.MoveDown  -> acc.y -= 1f
                    //@formatter:on
                }
            }

            if (vel dot acc < 0.5)
                acc.y *= 1.8f

            acc.magnitude = 10f
        }

        vel += acc * t
        pos += vel
        vel *= 0.9f
    }

    override fun toString(): String {
        return "$pos, $vel"
    }

    var graphics: Graphics? = null

    inner class Graphics {
        val mesh: Mesh = Mesh(
            Mesh.Triangles,
            Mesh.StaticDraw,
            intArrayOf(0, 1, 2, 1, 2, 3),
            (Vector2.arrayOf(
                -1f, -1f,
                1f, -1f,
                -1f, 1f,
                1f, 1f,
            ) * (16f)) as Array<Vector>,
            Vector2.arrayOf(
                0f, 0f,
                1f, 0f,
                0f, 1f,
                1f, 1f,
            ) as Array<Vector>,
        )

        private val _playerWorldMatrix = Matrix3x3.identity()
        private val _cursorWorldMatrix = Matrix3x3.identity()

        private val _playerTexture = FileSystem.openResource("Player.png", FileAccessMode.Read)!!.use { Texture(it) }
        private val _cursorTexture = FileSystem.openResource("Cursor.png", FileAccessMode.Read)!!.use { Texture(it) }

        init {
            _cursorWorldMatrix[0, 0] = 0.6f
            _cursorWorldMatrix[1, 1] = 0.6f
        }

        fun render(shader: Shader) {
            _playerWorldMatrix[2, 0] = pos.x
            _playerWorldMatrix[2, 1] = pos.y

            val sin = -vel.normalized dot Vector2(1f, 0f)
            val cos = vel.normalized dot Vector2(0f, 1f)
            _playerWorldMatrix[0, 0] = cos
            _playerWorldMatrix[1, 0] = -sin
            _playerWorldMatrix[0, 1] = sin
            _playerWorldMatrix[1, 1] = cos

            shader.bind()
            shader.setUniform("worldTransform", _playerWorldMatrix)

            glActiveTexture(GL_TEXTURE0)
            _playerTexture.bind()
            mesh.render(shader)


            _cursorWorldMatrix[2, 0] = mouse.x
            _cursorWorldMatrix[2, 1] = mouse.y

            shader.setUniform("worldTransform", _cursorWorldMatrix)

            _cursorTexture.bind()
            mesh.render(shader)
        }
    }
}