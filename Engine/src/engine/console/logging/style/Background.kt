package engine.console.logging.style

@Suppress("unused")
class Background(value: Int) : Style(value) {
    companion object {
        val Black = Background(40)
        val Red = Background(41)
        val Green = Background(42)
        val Orange = Background(43)
        val Blue = Background(44)
        val Magenta = Background(45)
        val Cyan = Background(46)
        val Grey = Background(47)

        val LightGrey = Background(7)

        val DarkGrey = Background(100)
        val LightRed = Background(101)
        val LightGreen = Background(102)
        val DarkYellow = Background(103)
        val LightBlue = Background(104)
        val LightMagenta = Background(105)
        val LightCyan = Background(106)
        val White = Background(107)
    }
}