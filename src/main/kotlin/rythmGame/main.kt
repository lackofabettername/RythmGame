package rythmGame

import engine.Engine
import engine.console.logging.Log
import engine.console.logging.LogLevel
import engine.console.logging.style.Foreground
import rythmGame.rendering.MainRenderLogic

fun main() {
    Log.LogLevel = LogLevel.Debug
    Log.IncludeTrace = true
    Log.styleCategory("Client", Foreground.DarkYellow)
    Log.styleCategory("ClientLogic", Foreground.Orange)
    Log.styleCategory("Server", Foreground.Green)
    Log.styleCategory("ServerLogic", Foreground.LightGreen)
    Log.styleCategory("Application", Foreground.LightRed)

    val engine = Engine()
    engine.RenderLogic = MainRenderLogic()

    engine.run()
}