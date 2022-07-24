package rythmgame2.common

import ecs.ECS
import ecs.Entity
import ecs.System

object MoveSys : System {
    override val keys = setOf(TransformComp, VelComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val pos = ecs[entity, TransformComp].Pos
        val vel = ecs[entity, VelComp]

        pos += vel.vel
        vel.vel *= vel.friction
    }
}

object AccelerateSys : System {
    override val keys = setOf(VelComp, AccComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val vel = ecs[entity, VelComp].vel
        val force = ecs[entity, AccComp].force
        val acc = ecs[entity, AccComp].acc

        vel += force + acc

        force.clear()
    }
}

