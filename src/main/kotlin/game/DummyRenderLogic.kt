package game

import engine.application.RenderInfo
import engine.application.Window
import engine.application.RenderLogic
import engine.application.events.*
import engine.console.ConsoleCommand
import logging.Log
import util.Vector2

class DummyRenderLogic(
    val client: DummyClientLogic
) : RenderLogic {
    lateinit var renderInfo: RenderInfo

    val gui = GUI()

    val playerPos = Vector2()

    var state = false
    var v = 0f

    var frameCount = 0

    override fun onStart(renderInfo: RenderInfo) {
        this.renderInfo = renderInfo
        gui.initialize(renderInfo.Window)

        gui.addWindow(debugGUI(this))
    }

    override fun onUpdate() {
    }

    override fun onRender() {
        //Log.trace("RenderLogic", "frame #$frameCount")
        ++frameCount

        renderInfo.Window.setClearColor(1f, playerPos.y, 0f, 0f)
        renderInfo.Window.clear(Window.ColorBuffer)

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
                        renderInfo.enqueueEvent(
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