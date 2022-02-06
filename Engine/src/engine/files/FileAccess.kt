package engine.files

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.channels.Channels
import java.nio.channels.FileChannel

class FileAccess(
    val Channel: FileChannel,
    val AccessMode: FileAccessMode
) : AutoCloseable {
    val IsOpen get() = Channel.isOpen

    //Stores the flush functions of the different writers that may be initiated
    private val _onClose = ArrayList<() -> Unit>()

    val Reader by lazy { Channels.newReader(Channel, FileSystem.Charset).buffered() }
    val Writer by lazy {
        val newWriter = Channels.newWriter(Channel, Charsets.UTF_8)
        _onClose += { newWriter.flush() }
        newWriter
    }

    val OIS by lazy { ObjectInputStream(Channels.newInputStream(Channel)) }
    val OOS by lazy {
        val oos = ObjectOutputStream(Channels.newOutputStream(Channel))
        _onClose += { oos.flush() }
        oos
    }

    override fun close() {
        if (!IsOpen) return
        _onClose.forEach { it() }     // Flush all writers that may be active
        Channel.force(true) // Force the channel to update the file
        Channel.close()
    }
}