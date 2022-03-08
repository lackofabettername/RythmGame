package rythmGame.rendering

import engine.Engine
import engine.application.RenderLogic
import engine.application.Window
import engine.application.events.InputEvent
import rythmGame.simulation.ClientLogic

class MainRenderLogic(
) : RenderLogic {
    lateinit var engine: Engine
    lateinit var window: Window

    var client: ClientLogic? = null

    val gui = GUI()

    val gameRenderer by lazy { GameRenderLogic(window, client!!) }

    override fun initialize(window: Window) {
        gui.initialize(window)
    }

    override fun onStart(engine: Engine) {
        this.engine = engine
        this.window = engine.Window!!

        gui.Windows += SettingsGUI(gui, this, engine)
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