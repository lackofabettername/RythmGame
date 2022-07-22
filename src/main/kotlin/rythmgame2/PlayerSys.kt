package rythmgame2

import ecs.ECS
import ecs.Entity
import ecs.System
import engine.console.logging.Log

object PlayerSys : System {
    override val keys = setOf(PlayerComp, TransformComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val inp = ecs.Singleton[InputComp]

        val transform = ecs[entity, TransformComp]
        val player = ecs[entity, PlayerComp]

        for (key in inp.HeldKeys) {
            transform.Pos += player.keyBinds[key] ?: continue
        }
    }
}