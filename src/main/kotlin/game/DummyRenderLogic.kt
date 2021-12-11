package game

import engine.application.Window
import engine.application.WindowCallbacks
import engine.application.events.*
import engine.console.ConsoleCommand
import logging.Log
import java.util.function.Consumer

class DummyRenderLogic(
    val client: DummyClientLogic
) : WindowCallbacks {
    lateinit var parentLoopback: Consumer<ApplicationEvent>

    val gui = GUI()

    var state = false
    var v = 0f

    var frameCount = 0

    override fun onStart(window: Window, parentLoopback: Consumer<ApplicationEvent>) {
        this.parentLoopback = parentLoopback
        gui.initialize(window)

        gui.addWindow(debugGUI(this))
    }

    override fun onUpdate() {
    }

    override fun onRender(window: Window) {
        //Log.trace("RenderLogic", "frame #$frameCount")
        ++frameCount

        if (state) {
            window.setClearColor(v, 1f, 1f, 1f)
        } else {
            window.setClearColor(v, 0f, 0f, 1f)
        }
        //v = (v + 1) % 2

        //Log.trace("RenderLogic", "Rendering state $state")
        window.clear(Window.ColorBuffer)

        gui.render()
    }

    override fun onClose() {
    }

    override fun onInputEvent(event: InputEvent) {
        Log.trace("RenderLogic", "Handling event $event")
        if (event.EventType == InputEventType.Key) {
            if ((event as KeyEvent).Type == KeyEventType.Pressed) {
                when (event.Key) {
                    Key.Escape -> {
                        parentLoopback.accept(
                            ApplicationEvent(ConsoleCommand("exit", ""))
                        )
                    }
                    Key.Space -> {
                        state = !state
                        Log.trace("RenderLogic", "Updated state to $state")
                    }
                }
            }
        }
    }

}