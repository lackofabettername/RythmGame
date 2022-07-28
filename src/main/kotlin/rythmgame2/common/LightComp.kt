package rythmgame2.common

import ecs.Component
import ecs.ComponentKey

class LightComp : Component<LightComp> {
    override val key = LightComp

    companion object : ComponentKey<LightComp>
}