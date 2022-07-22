package rythmgame2

import ecs.ECS
import ecs.Entity
import ecs.System
import ecs.SystemType

object InputSys : System {
    override val type = SystemType.Render
    override val keys = setOf(InputComp)
    override fun invoke(ecs: ECS, entity: Entity) {
        val inputs = ecs[entity, InputComp]

        inputs.PressedKeys.clear()
        inputs.PressedButtons.clear()

        inputs.ReleasedKeys.clear()
        inputs.ReleasedButtons.clear()

        inputs.PMouse copyFrom inputs.Mouse
    }
}