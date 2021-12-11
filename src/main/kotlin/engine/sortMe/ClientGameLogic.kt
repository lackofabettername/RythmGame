package engine.sortMe

import engine.network.common.MessageConsumer

interface ClientGameLogic {

    fun initialize(messageConsumer : MessageConsumer)

    fun updateFrame(deltaTime: Long)

    fun close()
}