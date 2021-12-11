package game

import engine.network.common.MessageConsumer
import engine.sortMe.ClientGameLogic

class DummyClientLogic : ClientGameLogic {
    lateinit var messageConsumer: MessageConsumer

    override fun initialize(messageConsumer: MessageConsumer) {
        this.messageConsumer = messageConsumer
    }

    override fun updateFrame(deltaTime: Long) {
    }

    override fun close() {
    }
}