package rythmGame.rendering

import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.InputEvent
import rythmGame.simulation.ClientLogic

class MainRenderLogic(
    val client: ClientLogic
) : RenderLogic {
    lateinit var engine: Engine
    lateinit var window: Window

    val gui = GUI()

    lateinit var gameRenderer: GameRenderLogic

    override fun initialize(window: Window) {
        gui.initialize(window)
        gui.Windows += SettingsGUI(gui, this)
    }

    override fun onStart(engine: Engine) {
        this.engine = engine
        this.window = engine.Application!!.Window

        gameRenderer = GameRenderLogic(window, client)

    }

    override fun onUpdate() {

    }

    override fun onRender() {
        window.clear(Window.ColorBuffer)

        gui.render()
    }

    override fun onClose() {

    }

    override fun onInputEvent(event: InputEvent) {

    }
}