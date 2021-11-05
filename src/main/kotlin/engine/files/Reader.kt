package engine.files

import java.lang.AutoCloseable
import java.lang.Exception

abstract class Reader<T> : AutoCloseable {
    @Throws(Exception::class)
    abstract fun read(): T
}