package engine.application

import engine.Engine
import engine.application.events.InputEvent

interface RenderLogic {

    fun initialize(window: Window)

    fun onStart(engine: Engine)

    fun onUpdate()

    fun onRender()

    fun onClose()

    fun onInputEvent(event: InputEvent)

    companion object {
    }
}