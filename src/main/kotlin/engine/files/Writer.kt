package engine.files

import java.lang.AutoCloseable
import java.lang.Exception

abstract class Writer<T> : AutoCloseable {
    @Throws(Exception::class)
    abstract fun write(data: T)
}