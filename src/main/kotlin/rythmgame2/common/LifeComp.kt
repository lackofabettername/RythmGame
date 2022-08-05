package rythmgame2.common

import ecs.Component
import ecs.ComponentKey

class LifeComp(
    var life: Int
) : Component<LifeComp> {
    override val key = LifeComp

    companion object : ComponentKey<LifeComp>
}