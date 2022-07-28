package rythmgame2.common

import ecs.Component
import ecs.ComponentKey
import util.Matrix3x3
import util.Vector2
import kotlin.math.cos
import kotlin.math.sin

class TransformComp(
    val Pos: Vector2 = Vector2(0f,0f),
    var Rot: Float = 0f,
    val Scale: Vector2 = Vector2(1f, 1f),
) : Component<TransformComp> {
    val WorldTransform: Matrix3x3
        get() {
            val cos = cos(Rot)
            val sin = sin(Rot)
            return Matrix3x3(
                cos * Scale.x, -sin * Scale.y, Pos.x,
                sin * Scale.x, cos * Scale.y, Pos.y,
                0f, 0f, 1f
            )
        }

    companion object : ComponentKey<TransformComp>

    override val key = TransformComp
}

class VelComp(
    val vel: Vector2,
    var friction: Float = 1f
) : Component<VelComp> {
    companion object : ComponentKey<VelComp>

    override val key = VelComp
}

class AccComp(
    val force: Vector2 = Vector2(),
    val acc: Vector2 = Vector2()
) : Component<AccComp> {
    companion object : ComponentKey<AccComp>

    override val key = AccComp
}

