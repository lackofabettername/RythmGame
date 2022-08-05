package rythmgame2.common

import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType

object MoveSys : System {
    override val type = SystemType.Update
    override val keys = setOf(TransformComp, VelocityComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val pos = ecs[entity, TransformComp].Pos
        val vel = ecs[entity, VelocityComp]

        pos += vel.vel
        vel.vel *= vel.friction
    }
}

object AccelerateSys : System {
    override val type = SystemType.Update
    override val keys = setOf(VelocityComp, AccComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val vel = ecs[entity, VelocityComp].vel
        val force = ecs[entity, AccComp].force
        val acc = ecs[entity, AccComp].acc

        vel += force + acc

        force.clear()
    }
}

