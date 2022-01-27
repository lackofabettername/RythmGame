package engine.application

import engine.application.events.InputEvent

@Deprecated("")
class RenderInfo(
    val Window: Window,
    private val _parent: Application
) {
    fun enqueueEvent(event: InputEvent) = _parent.enqueueEvent(event)
}