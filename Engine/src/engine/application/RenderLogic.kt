package engine.application

import engine.Engine
import engine.application.events.InputEvent

interface RenderLogic {

    fun initialize(window: Window)

    fun onStart(engine: Engine)

    fun onUpdate()

    fun onRender()

    fun onInputEvent(event: InputEvent)

    fun onClose()
}