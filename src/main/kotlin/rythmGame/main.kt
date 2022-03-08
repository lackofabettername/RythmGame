package rythmGame

import engine.Engine
import engine.console.logging.Log
import engine.console.logging.LogLevel
import engine.console.logging.style.Foreground
import rythmGame.rendering.MainRenderLogic

fun main() {
    Log.LogLevel = LogLevel.Debug
    Log.styleCategory("Client", Foreground.DarkYellow)
    Log.styleCategory("ClientLogic", Foreground.Orange)
    Log.styleCategory("Server", Foreground.Green)

    val renderer = MainRenderLogic()

    val engine = Engine(null, null, renderer)

    engine.run()
}