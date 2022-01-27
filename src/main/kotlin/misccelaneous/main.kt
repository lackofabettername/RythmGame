package misccelaneous

import engine.Engine
import engine.console.logging.Log
import engine.console.logging.LogLevel
import misccelaneous.meBored.MeBored

fun main() {
    Log.LogLevel = LogLevel.Debug
    Log.IncludeTrace = false

    val serverGameLogic = DummyServerLogic()
    val clientGameLogic = DummyClientLogic()
    //val renderGameLogic = DummyRenderLogic(clientGameLogic)
    val renderGameLogic = MeBored(clientGameLogic)

    val engine = Engine(null, null, renderGameLogic)
    engine.run()
}