package rythmgame2

import ecs.Component
import ecs.ComponentKey
import engine.application.events.Key
import util.Vector2


class InputComp : Component<InputComp> {
    val PressedKeys = HashSet<Key>()
    val HeldKeys = HashSet<Key>()
    val ReleasedKeys = HashSet<Key>()

    val Mouse = Vector2()
    val PMouse = Vector2()

    val PressedButtons = HashSet<Int>()
    val HeldButtons = HashSet<Int>()
    val ReleasedButtons = HashSet<Int>()

    override val key = InputComp

    companion object : ComponentKey<InputComp>
}