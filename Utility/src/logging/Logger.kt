package logging

import logging.style.Style
import java.io.*
import java.util.concurrent.TimeUnit

class Logger {
    val output = try {
        PrintWriter(BufferedOutputStream(FileOutputStream("Output.log")))
    } catch (e: FileNotFoundException) {
        PrintWriter(OutputStream.nullOutputStream())
    }

    private val firstLogTime = System.currentTimeMillis()
    private val builder = StringBuilder(256)

    val categoryStyles = HashMap<String, MutableList<Style>>()

    var includeTrace = false
    private val traces = HashMap<List<StackTraceElement>, Int>()

    var indent = 0

    fun log(level: LogLevel, category: String?, message: String, ex: Throwable?, vararg styles: Style) {
        //region Clear _builder
        builder.setLength(0)
        //endregion

        //region Indent
        builder.append("\t".repeat(indent))
        //endregion

        //region Time
        val time: Long = System.currentTimeMillis() - firstLogTime
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60
        //val millis = (TimeUnit.MILLISECONDS.toMillis(time) % 1000) / 10

        if (minutes <= 9) builder.append('0')
        builder.append("$minutes:")

        if (seconds <= 9) builder.append('0')
        builder.append("$seconds ")

        //if (millis <= 9) builder.append('0')
        //builder.append("$millis ")
        //endregion

        if (includeTrace) {
            var trace = Thread.currentThread().stackTrace.asList()
            trace = trace.subList(3, trace.size)
            traces.putIfAbsent(trace, traces.size)
            builder.append(String.format("%03X ", traces[trace]))
        }

        //region Level
        builder.append("${level.name}:   ", 0, 7)
        //endregion

        //region Category
        if (category != null) {
            builder.append("[$category] ")
        }
        //endregion

        //region Style
        val defaultStyle = StringBuilder()
        for (style in styles) {
            defaultStyle.append(style)
        }
        builder.append(defaultStyle)

        if (category in categoryStyles) {
            for (style in categoryStyles[category]!!) {
                builder.append(style)
            }
        }
        //endregion

        //region Message
        builder.append(message.replace(Style.Reset.toString(), defaultStyle.toString()))
        //endregion

        //region Exception
        if (ex != null) {
            val writer = StringWriter(256)
            ex.printStackTrace(PrintWriter(writer))
            builder.append('\n')
            builder.append(writer.toString().trim { it <= ' ' })
        }
        //endregion

        //region Out
        builder.append(Style.Clear)
        println(builder)
        output.println(builder)
        output.flush()
        //endregion
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

        if (traces.size > 0) {
            val temp = ArrayList<List<StackTraceElement>?>()
            for (i in 0 until traces.size) temp += null
            traces.forEach { (trace, id) -> temp[id] = trace }
            for ((i, trace) in temp.withIndex()) {
                output.println(String.format("%03X %s", i, trace))
            }
        }

        output.flush()
        output.close()
    }
}