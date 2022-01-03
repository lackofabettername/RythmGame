package engine.files

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.channels.Channels
import java.nio.channels.FileChannel

class FileAccess(private val _channel: FileChannel, val AccessMode: FileAccessMode) : AutoCloseable {
    val IsOpen get() = _channel.isOpen

    //Stores the flush functions of the different writers that may be initiated
    private val _onClose = ArrayList<() -> Unit>()

    val Reader by lazy { Channels.newReader(_channel, FileSystem.Charset).buffered() }
    val Writer by lazy {
        val newWriter = Channels.newWriter(_channel, Charsets.UTF_8)
        _onClose += { newWriter.flush() }
        newWriter
    }

    val OIS by lazy { ObjectInputStream(Channels.newInputStream(_channel)) }
    val OOS by lazy {
        val oos = ObjectOutputStream(Channels.newOutputStream(_channel))
        _onClose += { oos.flush() }
        oos
    }

    override fun close() {
        if (!IsOpen) return
        _onClose.forEach { it() }     // Flush all writers that may be active
        _channel.force(true) // Force the channel to update the file
        _channel.close()
    }
}