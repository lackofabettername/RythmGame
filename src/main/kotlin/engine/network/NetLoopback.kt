package engine.network

import java.util.concurrent.ArrayBlockingQueue
import engine.network.NetPacket
import engine.network.NetLoopback

class NetLoopback {
    private val _messages = ArrayBlockingQueue<NetPacket>(QueueCapacity, true)

    val isEmpty get() = _messages.isEmpty()

    fun getMessage() = _messages.poll()

    fun clear() {
        _messages.clear()
    }

    fun sendPacket(message: NetPacket) = _messages.offer(message)

    companion object {
        const val QueueCapacity = 512
    }
}