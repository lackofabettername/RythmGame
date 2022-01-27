package engine.console.logging

enum class LogLevel(val level: Int) {
    /** No logging at all. */
    None(6),

    /** Critical errors. The application may no longer work correctly.  */
    Error(5),

    /** Important warnings. The application will continue to work correctly.  */
    Warn(4),

    /** Informative messages. Typically used for deployment.  */
    Info(3),

    /** Useful during development.  */
    Debug(2),

    /** A lot of information is logged, so this level is usually only needed when debugging a problem.  */
    Trace(1);
}