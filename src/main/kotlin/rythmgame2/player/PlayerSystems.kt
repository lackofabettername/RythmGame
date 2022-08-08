package rythmgame2.player

import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType
import rythmgame2.common.InputComp
import rythmgame2.common.TransformComp
import rythmgame2.common.VelocityComp
import util.Vector2

object PlayerSys : System {
    override val type = SystemType.Update
    override val keys = setOf(PlayerComp, TransformComp, VelocityComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val inp = ecs.Singleton[InputComp]

        val (transform, velocity) = ecs[entity, TransformComp, VelocityComp]
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

        var dash = false
        for (key in inp.PressedKeys) {
            val input = player.keyBinds[key] ?: continue
            when (input) {
                PlayerInput.Dash -> dash = true
            }
        }

        if (move.magnitude > 0 && player.dashing <= 0) {
            //move.magnitude = 10f
            move.magnitude = 10f * (1 - velocity.friction)
            if (dash && player.dashing < 60) {
                move *= 5
                player.dashing = 5
                velocity.friction = 1f
            }

            velocity.vel += move
        }

        if (player.dashing-- == 0) {
            velocity.friction = 0f
        }
        if (velocity.vel.magnitude > 0) {
            transform.Rot = velocity.vel.heading
        }
    }
}

object Temp {

}