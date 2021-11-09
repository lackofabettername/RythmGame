package engine.files

import logging.Log
import java.io.*

class FileAccess(
    file: File,
    val Mode: FileAccessMode,
    val Type: FileType
) : AutoCloseable {
    private val _writer: Writer<*>?
    private val _reader: Reader<*>?

    init {
        when (Type) {
            FileType.Objects -> {
                _writer = if (Mode === FileAccessMode.Writer) ObjectWriter(file) else null
                _reader = if (Mode === FileAccessMode.Reader) ObjectReader(file) else null
            }
            FileType.Strings -> {
                _writer = if (Mode === FileAccessMode.Writer) StringWriter(file) else null
                _reader = if (Mode === FileAccessMode.Reader) StringReader(file) else null
            }
            else -> {
                _writer = null
                _reader = null
            }
        }
    }

    override fun close() {
        try {
            _writer?.close()
            _reader?.close()
        } catch (e: Exception) {
            Log.error("FileAccess", e)
        }
    }

    fun readObject(): Any? {
        if (Mode !== FileAccessMode.Reader) return null
        if (Type !== FileType.Objects) return null
        try {
            return (_reader as ObjectReader).read()
        } catch (e: IOException) {
            Log.error("", e)
        } catch (e: ClassNotFoundException) {
            Log.error("", e)
        }
        return null
    }

    fun writeObject(`object`: Any?) {
        if (Mode != FileAccessMode.Writer) return
        if (Type != FileType.Objects) return
        try {
            (_writer as ObjectWriter).write(`object`)
        } catch (e: IOException) {
            Log.error("", e)
        }
    }

    fun readString(): String? {
        if (Mode !== FileAccessMode.Reader) return null
        if (Type !== FileType.Strings) return null
        try {
            return (_reader as StringReader).read()
        } catch (e: IOException) {
            Log.error("", e)
        }
        return null
    }

    fun writeString(string: String?) {
        if (Mode != FileAccessMode.Writer) return
        if (Type != FileType.Strings) return
        try {
            (_writer as StringWriter).write(string)
        } catch (e: IOException) {
            Log.error("", e)
        }
    }

    class ObjectWriter constructor(file: File) : Writer<Any?>() {
        private val _stream = ObjectOutputStream(FileOutputStream(file))

        @Throws(IOException::class)
        override fun write(data: Any?) {
            _stream.writeObject(data)
        }

        @Throws(IOException::class)
        override fun close() {
            _stream.close()
        }
    }

    class StringWriter(file: File) : Writer<String?>() {
        private val _stream = PrintWriter(file)

        @Throws(IOException::class)
        override fun write(data: String?) {
            _stream.println(data)
        }

        override fun close() {
            _stream.flush()
            _stream.close()
        }
    }

    class ObjectReader(file: File) : Reader<Any?>() {
        private val _stream = ObjectInputStream(FileInputStream(file))

        @Throws(IOException::class, ClassNotFoundException::class)
        override fun read(): Any {
            return _stream.readObject()
        }

        @Throws(IOException::class)
        override fun close() {
            _stream.close()
        }
    }

    class StringReader(file: File) : Reader<String?>() {
        private val _stream = BufferedReader(FileReader(file))

        @Throws(IOException::class)
        override fun read(): String? {
            return _stream.readLine()
        }

        @Throws(IOException::class)
        override fun close() {
            _stream.close()
        }
    }
}