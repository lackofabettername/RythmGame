package engine.sortMe

interface ClientGameLogic {

    fun initialize()

    fun updateFrame(deltaTime: Long)

    fun close()
}