package rythmgame2.player

import ecs.ECS
import ecs.Entity
import ecs.System
import rythmgame2.common.InputComp
import rythmgame2.common.TransformComp
import util.Vector2
import kotlin.math.PI

object PlayerSys : System {
    override val keys = setOf(PlayerComp, TransformComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val inp = ecs.Singleton[InputComp]

        val transform = ecs[entity, TransformComp]
        val player = ecs[entity, PlayerComp]

        val move = Vector2()
        for (key in inp.HeldKeys) {
            val input = player.keyBinds[key] ?: continue
            when (input) {
                PlayerInput.Up -> move.y += 1
                PlayerInput.Left -> move.x -= 1
                PlayerInput.Down -> move.y -= 1
                PlayerInput.Right -> move.x += 1
            }
        }
        if (move.magnitude > 0) {
            move.magnitude = 10f

            transform.Pos += move
            transform.Rot = move.heading - PI.toFloat() / 2
        }
    }
}