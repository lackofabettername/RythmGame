@file:Suppress("unused")

package engine.files

import engine.console.CVar
import engine.console.CVarValueType
import engine.console.Console
import logging.Log
import java.io.File
import java.io.IOException

object FileSystem {
    //TODO: Check if file access is allowed when opening or closing.
    val RegisteredExtensions = HashMap(
        mapOf(
            "txt" to FileType.Strings,
            "ser" to FileType.Objects,
            "CFG" to FileType.Strings,
        )
    )
    private val _files = HashMap<String, FileAccess>()

    fun close() {
        for (fileIO in _files.values) {
            fileIO.close()
        }
        _files.clear()
    }

    fun openFile(fileName: String, mode: FileAccessMode, type: FileType): FileAccess? {
        return try {
            val fileIO = FileAccess(File(fileName), mode, type)
            val preexisting = getFileAccessor(fileName)

            if (preexisting != null) {
                preexisting.close()
                _files.replace(fileName, fileIO)
            } else {
                _files[fileName] = fileIO
            }

            fileIO
        } catch (e: IOException) {
            Log.error("FileSystem", "Could not open file $fileName", e)
            null
        }
    }

    @JvmStatic
    fun openFile(fileName: String, mode: FileAccessMode): FileAccess? {
        //val extension = fileName.substring(fileName.lastIndexOf(".") + 1)
        val extension = File(fileName).extension
        val type = RegisteredExtensions.getOrDefault(extension, FileType.Unkown)
        if (type == FileType.Unkown) Log.warn("FileSystem", "Unknown fileExtension: .$extension")
        return openFile(fileName, mode, type)
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
        return if (reader != null && reader.Mode == FileAccessMode.Read) {
            reader.readObject()
        } else {
            Log.warn("FileSystem", "Attempting to write to an unopened or read-only file!")
            null
        }
    }

    @JvmStatic
    fun writeObject(fileName: String, `object`: Any?) {
        val writer = getFileAccessor(fileName)
        if (writer != null && writer.Mode === FileAccessMode.Write) {
            writer.writeObject(`object`)
        } else {
            Log.warn("FileSystem", "Attempting to write to an unopened or read-only file!")
        }
    }

    fun readString(fileName: String): String? {
        val reader = getFileAccessor(fileName)
        if (reader != null && reader.Mode === FileAccessMode.Read) {
            return reader.readString()
        } else {
            Log.warn("FileSystem", "Attempting to write to an unopened or read-only file!")
        }
        return null
    }

    fun writeString(fileName: String, string: String?) {
        val writer = getFileAccessor(fileName)
        if (writer != null && writer.Mode === FileAccessMode.Write) {
            writer.writeString(string)
        } else {
            Log.warn("FileSystem", "Attempting to write to an unopened or read-only file!")
        }
    }

    fun loadConfiguration(console: Console) {
        //TODO:
        // - Read console variables from a file.
        // - Undecided: Variables must already exist.
        // --- Defined by a game-specific manifest
        // --- or from some kind of core systems registry.

        Log.info("FileSystem", "Loading Configuration...")
        Log.Indent++

        openFile("System.CFG", FileAccessMode.Read)
            ?.use { inp ->
                var line = inp.readString()

                while (line != null) {
                    Log.trace("FileSystem", line)

                    if (line.startsWith("CVar")) {
                        val parts = line.split(", ")
                        val name = parts[0].drop(5)
                        val data = parts[2].dropLast(1)
                        val cvar = when (parts[1]) {
                            CVarValueType.Text.name -> CVar(name, data)
                            CVarValueType.Value.name -> CVar(name, data.toInt())
                            CVarValueType.Flag.name -> CVar(name, data.toBoolean())
                            else -> null
                        }
                        if (cvar != null)
                            console.registerCVar(cvar)
                    }

                    line = inp.readString()
                }
            }

        Log.Indent--
        Log.info("FileSystem", "Configuration Loaded")
    }

    fun writeConfiguration(console: Console) {
        //TODO:
        // - Write out the settings of the engine or game to a file...
        // - Implement methods for saving out game state data?

        openFile("System.CFG", FileAccessMode.Write)
            ?.use { out ->

                //Print all CVars in alphabetical order
                for (cvar in console.cVars.stream()
                    .sorted { a, b -> a.Name.lowercase().compareTo(b.Name.lowercase()) })
                {
                    out.writeString("CVar{${cvar.Name}, ${cvar.Type}, ${cvar.get()}}")
                }
            }
    }

    private fun getFileAccessor(fileName: String): FileAccess? {
        return _files.getOrDefault(fileName, null)
    }
}