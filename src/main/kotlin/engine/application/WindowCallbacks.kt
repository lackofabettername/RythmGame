package engine.application

import engine.application.events.ApplicationEvent
import engine.application.events.InputEvent
import java.util.function.Consumer

interface WindowCallbacks {

    fun onStart(window: Window, parentLoopback: Consumer<ApplicationEvent>)

    fun onUpdate()

    fun onRender(window: Window)

    fun onClose()

    fun onInputEvent(event: InputEvent)

    companion object {
    }
}