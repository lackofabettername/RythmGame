package rythmgame2.player

import ecs.Component
import ecs.ComponentKey
import ecs.ECS
import ecs.Entity
import engine.application.events.Key
import engine.application.rendering.Mesh
import engine.application.rendering.Texture
import engine.files.FileAccessMode
import engine.files.FileSystem
import rythmgame2.common.RenderComp
import rythmgame2.common.TransformComp
import rythmgame2.common.VelocityComp
import space.Vector
import space.Vector2
import space.times

class PlayerComp(
    val keyBinds: HashMap<Key, PlayerInput>,
) : Component<PlayerComp> {
    var dashing = 0

    override val key = PlayerComp

    companion object : ComponentKey<PlayerComp> {
        fun createPlayer(ecs: ECS): Entity {
            val player = ecs.createEntity()

            val mesh = Mesh(
                Mesh.Triangles,
                Mesh.StaticDraw,
                intArrayOf(0, 1, 2, 1, 2, 3),
                (Vector2.arrayOf(
                    -1f, -1f,
                    1f, -1f,
                    -1f, 1f,
                    1f, 1f
                ) * 32f) as Array<Vector>,
                Vector2.arrayOf(
                    0f, 0f,
                    1f, 0f,
                    0f, 1f,
                    1f, 1f
                ) as Array<Vector>
            )

            val texture = FileSystem.openResource("Player.png", FileAccessMode.Read)!!
                .use { Texture.load2D(it) }

            ecs[player] += TransformComp(
                Vector2(450.0f, 300.0f),
                0.0f,
                Vector2(0.5f)
            )
            ecs[player] += VelocityComp(Vector2(), 0.4f)
            ecs[player] += RenderComp(
                mesh,
                1,
                0,
                texture,
            )
            ecs[player] += PlayerComp(
                hashMapOf(
                    Key.W to PlayerInput.Up,
                    Key.A to PlayerInput.Left,
                    Key.S to PlayerInput.Down,
                    Key.D to PlayerInput.Right,
                    Key.LShift to PlayerInput.Dash,
                )
            )

            return player
        }
    }
}