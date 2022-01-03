package engine.files

import engine.files.FileAccessMode.*
import logging.Log
import java.io.*
import java.nio.charset.Charset as CharsetClass

object FileSystem {
    val Charset: CharsetClass by lazy { CharsetClass.defaultCharset() }

    private val _files = HashMap<String, FileAccess>()

    fun openFile(fileName: String, accessMode: FileAccessMode, createIfMissing: Boolean = false): FileAccess? {
        return openActual(fileName, accessMode, File(fileName), createIfMissing)
    }

    fun openResource(fileName: String, accessMode: FileAccessMode, createIfMissing: Boolean = false): FileAccess? {
        val uri = javaClass.classLoader.getResource(fileName)?.toURI()
        return openActual(fileName, accessMode, uri?.let { File(uri) } ?: File(""), createIfMissing)
    }

    private fun openActual(
        fileName: String,
        accessMode: FileAccessMode,
        file: File,
        createIfMissing: Boolean
    ): FileAccess? {
        if (fileName in this) {
            Log.warn("FileSystem", "$fileName is already open! Closing the file...")
            closeFile(fileName)
        }

        if (!file.exists()) {
            if (!createIfMissing) {
                Log.warn("FileSystem", "$fileName doesn't exist!")
                return null
            }

            Log.info("FileSystem", "$fileName is missing. Creating it...")
            file.createNewFile()
        }

        val fileAccess = FileAccess(
            when (accessMode) {
                //@formatter:off
                    Read      -> FileInputStream(file).             channel
                    Write     -> FileOutputStream(file).            channel
                    ReadWrite -> RandomAccessFile(file, "rw").channel
                    //@formatter:on
            }, accessMode
        )
        _files[fileName] = fileAccess
        return fileAccess

    }

    fun closeFile(fileName: String) {
        _files[fileName]?.close()
        _files -= fileName
    }

    fun close() {
        _files.values.forEach { it.close() }
        _files.clear()
    }

    operator fun get(fileName: String) = _files[fileName]

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

    operator fun contains(fileName: String) = fileName in _files && _files[fileName]!!.IsOpen

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
}