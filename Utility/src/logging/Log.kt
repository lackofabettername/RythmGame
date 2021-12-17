package logging

import logging.style.Foreground
import logging.style.Style
import logging.LogLevel.*

@Suppress("unused")
object Log {
    var LogLevel = Trace
        set(level) {
            field = level

            // @formatter:off
            ERROR = LogLevel >= Error
            WARN  = LogLevel >= Warn
            INFO  = LogLevel >= Info
            DEBUG = LogLevel >= Debug
            TRACE = LogLevel >= Trace
            // @formatter:on

            _logger.includeTrace = TRACE
        }

    private val _logger = Logger()

    var Indent by _logger::indent
    var IncludeTrace by _logger::includeTrace

    // @formatter:off
    private var ERROR = false
    private var WARN  = false
    private var INFO  = false
    private var DEBUG = false
    private var TRACE = false

    val ErrorStyle = arrayOf(Style.Clear, Foreground.Red)
    val WarnStyle  = arrayOf(Style.Clear, Foreground.Orange)
    val InfoStyle  = arrayOf(Style.Clear)
    val DebugStyle = arrayOf(Style.Clear, Foreground.Cyan)
    val TraceStyle = arrayOf(Style.Clear, Foreground.Magenta)
    // @formatter:on

    init {
        Runtime.getRuntime().addShutdownHook(Thread { close() })
        LogLevel = LogLevel //DO NOT REMOVE!
    }

    fun styleCategory(category: String, vararg styles: Style) {
        if (category !in _logger.categoryStyles)
            _logger.categoryStyles[category] = ArrayList()
        _logger.categoryStyles[category]!! += styles
    }

    fun unstyleCategory(category: String, vararg styles: Style) {
        if (category !in _logger.categoryStyles)
            _logger.categoryStyles[category]!! -= styles
    }

    fun clearCategoryStyle(category: String?) {
        if (_logger.categoryStyles.containsKey(category))
            _logger.categoryStyles.get(category)!!.clear()
    }

    /**
     * @return the full name of the class calling this method.
     */
    val callerName: String
        get() {
            val stackTrace = Thread.currentThread().stackTrace
            return stackTrace[2].className
        }

    /**
     * @return the name of the class calling this method.
     */
    val callerNameSimple: String
        get() {
            val stackTrace = Thread.currentThread().stackTrace
            return stackTrace[2].className.replace(".*\\.".toRegex(), "")
        }

    //region Error
    fun error(category: String, message: String, ex: Throwable?) {
        if (ERROR) _logger.log(Error, category, message, ex, *ErrorStyle)
    }

    fun error(message: String, ex: Throwable?) {
        if (ERROR) _logger.log(Error, null, message, ex, *ErrorStyle)
    }

    fun error(category: String, message: String) {
        if (ERROR) _logger.log(Error, category, message, null, *ErrorStyle)
    }

    fun error(message: String) {
        if (ERROR) _logger.log(Error, null, message, null, *ErrorStyle)
    }
    //endregion

    //region Warn
    fun warn(category: String, message: String, ex: Throwable?) {
        if (WARN) _logger.log(Warn, category, message, ex, *WarnStyle)
    }

    fun warn(message: String, ex: Throwable?) {
        if (WARN) _logger.log(Warn, null, message, ex, *WarnStyle)
    }

    fun warn(category: String, message: String) {
        if (WARN) _logger.log(Warn, category, message, null, *WarnStyle)
    }

    fun warn(message: String) {
        if (WARN) _logger.log(Warn, null, message, null, *WarnStyle)
    }
    //endregion

    //region Info
    fun info(category: String, message: String, ex: Throwable?) {
        if (INFO) _logger.log(Info, category, message, ex, *InfoStyle)
    }

    fun info(message: String, ex: Throwable?) {
        if (INFO) _logger.log(Info, null, message, ex, *InfoStyle)
    }

    fun info(category: String, message: String) {
        if (INFO) _logger.log(Info, category, message, null, *InfoStyle)
    }

    fun info(message: String) {
        if (INFO) _logger.log(Info, null, message, null, *InfoStyle)
    }
    //endregion

    //region Debug
    fun debug(category: String, message: String, ex: Throwable?) {
        if (DEBUG) _logger.log(Debug, category, message, ex, *DebugStyle)
    }

    fun debug(message: String, ex: Throwable?) {
        if (DEBUG) _logger.log(Debug, null, message, ex, *DebugStyle)
    }

    fun debug(category: String, message: String) {
        if (DEBUG) _logger.log(Debug, category, message, null, *DebugStyle)
    }

    fun debug(message: String) {
        if (DEBUG) _logger.log(Debug, null, message, null, *DebugStyle)
    }
    //endregion

    //region Trace
    fun trace(category: String, message: String, ex: Throwable?) {
        if (TRACE) _logger.log(Trace, category, message, ex, *TraceStyle)
    }

    fun trace(message: String, ex: Throwable?) {
        if (TRACE) _logger.log(Trace, null, message, ex, *TraceStyle)
    }

    fun trace(category: String, message: String) {
        if (TRACE) _logger.log(Trace, category, message, null, *TraceStyle)
    }

    fun trace(message: String) {
        if (TRACE) _logger.log(Trace, null, message, null, *TraceStyle)
    }
    //endregion

    fun flush() {
        _logger.output.flush()
    }

    fun close() {
        _logger.close()
    }
}