package engine.application

import engine.application.events.InputEvent

interface RenderLogic {

    fun onStart(renderInfo: RenderInfo)

    fun onUpdate()

    fun onRender()

    fun onClose()

    fun onInputEvent(event: InputEvent)

    companion object {
    }
}