package rythmgame2.common

import ecs.Component
import ecs.ComponentKey
import engine.application.rendering.FrameBuffer

class ShadowBufferComp(
    val Buffer: FrameBuffer,
) : Component<ShadowBufferComp> {
    override val key = ShadowBufferComp

    companion object : ComponentKey<ShadowBufferComp>
}

class ShadowComp : Component<ShadowComp> {
    override val key = ShadowComp

    companion object : ComponentKey<ShadowComp>
}