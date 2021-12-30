package engine.files2

import engine.files.FileAccessMode
import engine.files.FileAccessMode.*
import logging.Log
import java.io.*
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import kotlin.reflect.KCallable
import java.nio.charset.Charset as CharsetClass

object FileSystem2 {
    val Charset: CharsetClass by lazy { CharsetClass.defaultCharset() }

    private val _files = HashMap<String, FileAccess>()

    fun openFile(fileName: String, accessMode: FileAccessMode): FileAccess? {
        if (fileName in _files) {
            Log.warn("FileSystem", "$fileName is already open! Closing the file...")
            closeFile(fileName)
        }

        val uri = javaClass.classLoader.getResource(fileName)?.toURI()
        if (uri == null) {
            Log.warn("FileSystem", "$fileName doesn't exist!")
            return null
        } else {
            val fileAccess = FileAccess(
                when (accessMode) {
                    //@formatter:off
                    Read      -> FileInputStream(File(uri)).             channel
                    Write     -> FileOutputStream(File(uri)).            channel
                    ReadWrite -> RandomAccessFile(File(uri), "rw").channel
                    //@formatter:on
                }, accessMode
            )
            _files[fileName] = fileAccess
            return fileAccess
        }
    }

    fun closeFile(fileName: String) {
        _files[fileName]?.close()
        _files -= fileName
    }

    fun close() {
        for (key in _files.keys) {
            _files[key]?.close()
            _files -= key
        }
    }

    /**
     * @return null if the file doesn't exist, is closed or the access mode isn't correct. Otherwise, returns the file
     */
    operator fun get(fileName: String, accessMode: FileAccessMode) = _files[fileName].also { file ->
        if (file == null) return null
        if (!file.IsOpen) {
            _files -= fileName
            return null
        }
        if (file.AccessMode != accessMode && file.AccessMode != ReadWrite) return null
    }

    operator fun contains(fileName: String) = fileName in _files

    /**
     * Writes the string to the given file.
     */
    fun writeString(fileName: String, string: String) {
        this[fileName, Write]?.Writer?.write(string)
    }

    fun appendString(fileName: String, string: String) {
        this[fileName, Write]?.Writer?.append(string)
    }

    fun readLine(fileName: String) = this[fileName, Read]?.Reader?.readLine()


    fun writeObject(fileName: String, `object`: Serializable) {
        this[fileName, Write]?.OOS?.writeObject(`object`)
    }

    fun readObject(fileName: String) = this[fileName, Read]?.OIS?.readObject()


    class FileAccess(private val _channel: FileChannel, val AccessMode: FileAccessMode) : AutoCloseable {
        val IsOpen get() =_channel.isOpen

        //Stores the flush functions of the different writers that may be initiated
        private val _onClose = ArrayList<() -> Unit>()

        val Reader by lazy { Channels.newReader(_channel, Charset).buffered() }
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
}

fun main() {
    val f = File("test.txt")
    val channel = FileOutputStream(f).channel
    val writer = Channels.newWriter(channel, Charsets.UTF_8)
    writer.write("hello world")
    writer.flush()
    writer.close()

    FileSystem2.openFile("file.txt", Write)
    FileSystem2.writeString("file.txt", "hej 123")
    FileSystem2.close()
}