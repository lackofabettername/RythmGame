package engine.application

import engine.application.events.ApplicationEvent
import engine.application.events.InputEvent
import java.util.function.Consumer

interface WindowCallbacks {

    fun onStart(parentLoopback: Consumer<ApplicationEvent>)

    fun onUpdate()

    fun onRender()

    fun onClose()

    fun onInputEvent(event: InputEvent)

    companion object {
        @Deprecated("")
        val Dummy = object : WindowCallbacks {
            override fun onStart(parentLoopback: Consumer<ApplicationEvent>) {}
            override fun onUpdate() {}
            override fun onRender() {}
            override fun onClose() {}
            override fun onInputEvent(event: InputEvent) {}
        }
    }
}