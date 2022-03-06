package engine.console.logging

import engine.console.logging.style.Style
import java.io.*
import java.util.concurrent.TimeUnit

class Logger {
    val output = try {
        PrintWriter(BufferedOutputStream(FileOutputStream("Output.log")))
    } catch (e: FileNotFoundException) {
        System.err.println("Could not open Log file")
        PrintWriter(OutputStream.nullOutputStream())
    }

    private val _firstLogTime = System.currentTimeMillis()
    private val _builder = StringBuilder(256)

    val CategoryStyles = HashMap<String, List<Style>>()

    var IncludeTrace = false
    private val _traces = HashMap<List<StackTraceElement>, Int>()

    var Indent = 0

    fun log(level: LogLevel, category: String?, message: String, ex: Throwable?, vararg styles: Style) {
        with(_builder) {

            //region Clear _builder
            setLength(0)
            //endregion

            //region Indent
            val indent = "\t".repeat(Indent)
            append(indent)
            //endregion

            //region Time
            val time: Long = System.currentTimeMillis() - _firstLogTime
            val minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60
            //val millis = (TimeUnit.MILLISECONDS.toMillis(time) % 1000) / 10

            if (minutes <= 9) append('0')
            append("$minutes:")

            if (seconds <= 9) append('0')
            append("$seconds ")

            //if (millis <= 9) builder.append('0')
            //builder.append("$millis ")
            //endregion

            if (IncludeTrace) {
                var trace = Thread.currentThread().stackTrace.asList()
                trace = trace.subList(3, trace.size)
                _traces.putIfAbsent(trace, _traces.size)
                append(String.format("%03X ", _traces[trace]))
            }

            //region Level
            append("${level.name}:   ", 0, 7)
            //endregion

            //region Category
            if (category != null) {
                for (style in CategoryStyles[category].orEmpty())
                    append(style)

                append("[$category] ")

                append(Style.Clear)
            }
            //endregion

            //region Style
            val defaultStyle = StringBuilder()
            for (style in styles) {
                defaultStyle.append(style)
            }
            append(defaultStyle)
            //endregion

            //region Message
            append(
                message
                    .replace(Style.Reset.toString(), defaultStyle.toString())
                    .prependIndent(indent) // Give multiline messages the same indent
                    .drop(Indent)          // Don't indent first line
            )
            //endregion

            //region Exception
            if (ex != null) {
                append("\n")
                append(ex.stackTraceToString().prependIndent(indent))
            }
            //endregion

            //region Out
            append(Style.Clear) // Don't leak any styles to the next line

            println(_builder)       // Print to console
            output.println(_builder)// Print to log
            output.flush()
            //endregion
        }
    }

    fun close() {
        output.println(
            """
            |
            |----------------
            |   End of log   
            |----------------
            |""".trimMargin()
        )

        if (_traces.size > 0) {
            val temp = ArrayList<List<StackTraceElement>?>()
            for (i in 0 until _traces.size) temp += null
            _traces.forEach { (trace, id) -> temp[id] = trace }
            for ((i, trace) in temp.withIndex()) {
                output.println(String.format("%03X %s", i, trace))
            }
        }

        output.flush()
        output.close()
    }
}