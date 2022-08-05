package rythmgame2.common

import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType

object LifeSys : System {
    override val type = SystemType.Update
    override val keys = setOf(LifeComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val life = ecs[entity, LifeComp]

        if (life.life-- <= 0) {
            ecs.removeEntity(entity)
        }
    }
}