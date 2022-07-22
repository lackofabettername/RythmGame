package rythmgame2

import ecs.Component
import ecs.ComponentKey
import engine.application.events.Key
import util.Vector2

class PlayerComp(
    val keyBinds: HashMap<Key, Vector2>
) : Component<PlayerComp> {
    override val key = PlayerComp

    companion object : ComponentKey<PlayerComp>
}