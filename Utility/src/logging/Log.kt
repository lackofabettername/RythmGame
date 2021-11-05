package logging

import logging.style.Foreground
import logging.style.Style

@Suppress("unused")
object Log {
    var logLevel = LogLevel.Trace
        set(level) {
            field = level

            // @formatter:off
            ERROR = logLevel >= LogLevel.Error
            WARN  = logLevel >= LogLevel.Warn
            INFO  = logLevel >= LogLevel.Info
            DEBUG = logLevel >= LogLevel.Debug
            TRACE = logLevel >= LogLevel.Trace
            // @formatter:on

            logger.includeTrace = TRACE
        }

    private val logger = Logger()

    var indent by logger::indent
    var includeTrace by logger::includeTrace

    // @formatter:off
    private var ERROR = false
    private var WARN  = false
    private var INFO  = false
    private var DEBUG = false
    private var TRACE = false

    val ErrorStyle = arrayOf<Style>(Style.Clear, Foreground.Red)
    val WarnStyle  = arrayOf<Style>(Style.Clear, Foreground.Orange)
    val InfoStyle  = arrayOf<Style>(Style.Clear)
    val DebugStyle = arrayOf<Style>(Style.Clear, Foreground.Cyan)
    val TraceStyle = arrayOf<Style>(Style.Clear, Foreground.Magenta)
    // @formatter:on

    init {
        Runtime.getRuntime().addShutdownHook(Thread { close() })
        logLevel = logLevel //DO NOT REMOVE!
    }

    fun styleCategory(category: String, vararg styles: Style) {
        if (category !in logger.categoryStyles)
            logger.categoryStyles[category] = ArrayList()
        logger.categoryStyles[category]!! += styles
    }

    fun unstyleCategory(category: String, vararg styles: Style) {
        if (category !in logger.categoryStyles)
            logger.categoryStyles[category]!! -= styles
    }

    fun clearCategoryStyle(category: String?) {
        if (logger.categoryStyles.containsKey(category))
            logger.categoryStyles.get(category)!!.clear()
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
        if (ERROR) logger.log(LogLevel.Error, category, message, ex, *ErrorStyle)
    }

    fun error(message: String, ex: Throwable?) {
        if (ERROR) logger.log(LogLevel.Error, null, message, ex, *ErrorStyle)
    }

    fun error(category: String, message: String) {
        if (ERROR) logger.log(LogLevel.Error, category, message, null, *ErrorStyle)
    }

    fun error(message: String) {
        if (ERROR) logger.log(LogLevel.Error, null, message, null, *ErrorStyle)
    }

    //endregion
    //region Warn
    fun warn(category: String, message: String, ex: Throwable?) {
        if (WARN) logger.log(LogLevel.Warn, category, message, ex, *WarnStyle)
    }

    fun warn(message: String, ex: Throwable?) {
        if (WARN) logger.log(LogLevel.Warn, null, message, ex, *WarnStyle)
    }

    fun warn(category: String, message: String) {
        if (WARN) logger.log(LogLevel.Warn, category, message, null, *WarnStyle)
    }

    fun warn(message: String) {
        if (WARN) logger.log(LogLevel.Warn, null, message, null, *WarnStyle)
    }

    //endregion
    //region Info
    fun info(category: String, message: String, ex: Throwable?) {
        if (INFO) logger.log(LogLevel.Info, category, message, ex, *InfoStyle)
    }

    fun info(message: String, ex: Throwable?) {
        if (INFO) logger.log(LogLevel.Info, null, message, ex, *InfoStyle)
    }

    fun info(category: String, message: String) {
        if (INFO) logger.log(LogLevel.Info, category, message, null, *InfoStyle)
    }

    fun info(message: String) {
        if (INFO) logger.log(LogLevel.Info, null, message, null, *InfoStyle)
    }

    //endregion
    //region Debug
    fun debug(category: String, message: String, ex: Throwable?) {
        if (DEBUG) logger.log(LogLevel.Debug, category, message, ex, *DebugStyle)
    }

    fun debug(message: String, ex: Throwable?) {
        if (DEBUG) logger.log(LogLevel.Debug, null, message, ex, *DebugStyle)
    }

    fun debug(category: String, message: String) {
        if (DEBUG) logger.log(LogLevel.Debug, category, message, null, *DebugStyle)
    }

    fun debug(message: String) {
        if (DEBUG) logger.log(LogLevel.Debug, null, message, null, *DebugStyle)
    }

    //endregion
    //region Trace
    fun trace(category: String, message: String, ex: Throwable?) {
        if (TRACE) logger.log(LogLevel.Trace, category, message, ex, *TraceStyle)
    }

    fun trace(message: String, ex: Throwable?) {
        if (TRACE) logger.log(LogLevel.Trace, null, message, ex, *TraceStyle)
    }

    fun trace(category: String, message: String) {
        if (TRACE) logger.log(LogLevel.Trace, category, message, null, *TraceStyle)
    }

    fun trace(message: String) {
        if (TRACE) logger.log(LogLevel.Trace, null, message, null, *TraceStyle)
    }

    //endregion

    fun flush() {
        logger.output.flush()
    }

    fun close() {
        logger.close()
    }
}