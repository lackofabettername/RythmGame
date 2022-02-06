package rythmGame

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
    val pos = Vector2()
    val vel = Vector2()

    fun update() {
        val acc = Vector2();
        for (input in inputs) {
            when (input) {
                ////@formatter:off
                PlayerInput.MoveLeft  -> acc.x -= 1f
                PlayerInput.MoveRight -> acc.x += 1f

                PlayerInput.MoveUp    -> acc.y += 1f
                PlayerInput.MoveDown  -> acc.y -= 1f
                //@formatter:on
            }
        }

        vel += acc.normalized * 6f
        pos += vel
        vel *= 0.95f
    }

    override fun toString(): String {
        return "$pos"
    }

    var graphics: Graphics? = null

    inner class Graphics {
        val mesh: Mesh = Mesh(
            Mesh.Triangles,
            Mesh.StaticDraw,
            intArrayOf(0, 1, 2, 1, 2, 3),
            (Vector2.arrayOf(
                0f, 0f,
                1f, 0f,
                0f, 1f,
                1f, 1f,
            ) * (128f / 2)) as Array<Vector>,
            Vector2.arrayOf(
                0f, 0f,
                1f, 0f,
                0f, 1f,
                1f, 1f,
            ) as Array<Vector>,
        )

        val worldMatrix = Matrix3x3.identity()

        val texture = FileSystem.openResource("Player.png", FileAccessMode.Read)!!.use { Texture(it) }

        fun render(shader: Shader) {
            //Log.debug("$pos")

            worldMatrix[2, 0] = pos.x
            worldMatrix[2, 1] = pos.y

            shader.bind()
            shader.setUniform("worldTransform", worldMatrix)

            glActiveTexture(GL_TEXTURE0)
            texture.bind()
            mesh.render(shader)
        }
    }
}