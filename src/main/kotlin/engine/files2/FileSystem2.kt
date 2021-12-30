package engine.files2

import engine.files.FileAccessMode
import logging.Log
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

object FileSystem2 {


    val files = HashMap<String, FileAccess>()

    fun openFile(fileName: String, accessMode: FileAccessMode) {
        if (fileName in files) {
            Log.warn("FileSystem", "$fileName is already open! Closing the file...")
            closeFile(fileName)
        }

        val uri = javaClass.classLoader.getResource(fileName)?.toURI()
        if (uri == null) {
            Log.warn("FileSystem", "$fileName doesn't exist!")
        } else {
            val file = File(uri)
            files[fileName] = FileAccess(when (accessMode) {
                //@formatter:off
                FileAccessMode.Read         -> FileInputStream(file).             channel
                FileAccessMode.Write        -> FileOutputStream(file).            channel
                FileAccessMode.RandomAccess -> RandomAccessFile(file, "rw").channel
                //@formatter:on
            }, accessMode)
        }
    }

    fun closeFile(fileName: String) {
        files[fileName]?.close()
        files -= fileName
    }

    operator fun get(fileName: String) = files[fileName]

    operator fun contains(fileName: String) = fileName in files

    /**
     * Writes the string to the given file. Returns true if more than 0 bytes where written.
     */
    fun writeString(fileName: String, string: String): Boolean {
        if (fileName !in files) return false
        val (channel, mode) = files[fileName]!!

        if (mode == FileAccessMode.Read) return false

        return channel.write(ByteBuffer.wrap(string.encodeToByteArray())) > 0
    }

    fun readString(fileName: String, delimiter: String): String? {
        if (fileName !in files) return null
        val (channel, mode) = files[fileName]!!

        if (mode == FileAccessMode.Write) return null

        var oup = StringBuilder()
        val buffer = ByteBuffer.allocate(1024)
        channel.read(buffer)
    }

    class FileAccess (val channel: FileChannel, val accessMode: FileAccessMode) {
        val buf = ByteBuffer.allocate(1024)

        fun close() {
            channel.close()
        }

        operator fun component1() = channel
        operator fun component2() = accessMode
    }
}

fun main() {
    FileSystem2.openFile("file.txt", FileAccessMode.Read)
}