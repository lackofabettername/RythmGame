package rythmgame2.common

import Color
import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType
import engine.application.rendering.Mesh
import space.Vector
import space.Vector2

object SpeedSmearSys : System {
    override val type = SystemType.Update
    override val keys = setOf(RenderComp, TransformComp, VelocityComp)

    override fun invoke(ecs: ECS, entity: Entity) {
        val (parent, transform, velocity) = ecs[entity, RenderComp, TransformComp, VelocityComp]

        if (velocity.vel.magnitude > 10) {
            val pos = transform.Pos.copy
            val previous = pos - velocity.vel

            val smear = ecs.createEntity()
            ecs[smear] += TransformComp(
                pos,
                (pos - previous).heading,
                Vector2(velocity.vel.magnitude, 3f)
            )
            ecs[smear] += LifeComp(10)
            ecs[smear] += RenderComp(
                Mesh(
                    Mesh.TriangleStrip,
                    Mesh.StaticDraw,
                    Vector2.arrayOf(
                        0f, 0f,
                        1f, 0f,
                        0f, 1f,
                        1f, 1f,
                    ) as Array<Vector>,
                    Array(4) { Color.rgb(0.3f, 0.3f, 0.3f) } as Array<Vector>
                ),
                2,
                parent.color,
                null
            )
        }
    }
}