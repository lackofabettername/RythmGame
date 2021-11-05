@file:Suppress("unused")

package engine.files

import logging.Log
import java.io.File

object FileSystem {
    //TODO: Check if file access is allowed when opening or closing.
    val RegisteredExtensions = HashMap(
        mapOf(
            Pair("txt", FileType.Strings),
            Pair("ser", FileType.Objects)
        )
    )
    private val _files = HashMap<String, FileAccess>()

    fun close() {
        for (fileIO in _files.values) {
            fileIO.close()
        }
        _files.clear()
    }

    fun openFile(fileName: String, mode: FileAccessMode, type: FileType) {
        val fileIO = FileAccess(File(fileName), mode, type)
        val preexisting = getFileAccessor(fileName)
        if (preexisting != null) {
            preexisting.close()
            _files.replace(fileName, fileIO)
        } else {
            _files[fileName] = fileIO
        }
    }

    @JvmStatic
    fun openFile(fileName: String, mode: FileAccessMode) {
        //val extension = fileName.substring(fileName.lastIndexOf(".") + 1)
        val extension = File(fileName).extension
        val type = RegisteredExtensions.getOrDefault(extension, FileType.Unkown)
        if (type == FileType.Unkown) Log.warn("FileSystem", "Unknown fileExtension: .$extension")
        openFile(fileName, mode, type)
    }

    @JvmStatic
    fun closeFile(fileName: String) {
        val fileIO = getFileAccessor(fileName)
        if (fileIO != null) {
            fileIO.close()
            _files.remove(fileName)
        }
    }

    @JvmStatic
    fun readObject(fileName: String): Any? {
        val reader = getFileAccessor(fileName)
        return if (reader != null && reader.Mode == FileAccessMode.Reader) {
            reader.readObject()
        } else {
            Log.warn("FileSystem", "Attempting to write to an unopened or read-only file!")
            null
        }
    }

    @JvmStatic
    fun writeObject(fileName: String, `object`: Any?) {
        val writer = getFileAccessor(fileName)
        if (writer != null && writer.Mode === FileAccessMode.Writer) {
            writer.writeObject(`object`)
        } else {
            Log.warn("FileSystem", "Attempting to write to an unopened or read-only file!")
        }
    }

    fun readString(fileName: String): String? {
        val reader = getFileAccessor(fileName)
        if (reader != null && reader.Mode === FileAccessMode.Reader) {
            return reader.readString()
        } else {
            Log.warn("FileSystem", "Attempting to write to an unopened or read-only file!")
        }
        return null
    }

    fun writeString(fileName: String, string: String?) {
        val writer = getFileAccessor(fileName)
        if (writer != null && writer.Mode === FileAccessMode.Writer) {
            writer.writeString(string)
        } else {
            Log.warn("FileSystem", "Attempting to write to an unopened or read-only file!")
        }
    }

    fun loadConfiguration() {
        //TODO:
        // - Read console variables from a file.
        // - Undecided: Variables must already exist.
        // --- Defined by a game-specific manifest
        // --- or from some kind of core systems registry.
    }

    fun writeConfiguration() {
        //TODO:
        // - Write out the settings of the engine or game to a file...
        // - Implement methods for saving out game state data?
    }

    private fun getFileAccessor(fileName: String): FileAccess? {
        return _files.getOrDefault(fileName, null)
    }
}