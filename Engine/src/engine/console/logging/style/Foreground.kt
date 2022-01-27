package engine.console.logging.style

@Suppress("unused")
class Foreground(value: Int) : Style(value) {

    companion object {
        val Black = Foreground(30)
        val Red = Foreground(31)
        val Green = Foreground(32)
        val Orange = Foreground(33)
        val Blue = Foreground(34)
        val Magenta = Foreground(35)
        val Cyan = Foreground(36)
        val LightGrey = Foreground(37)

        val DarkGrey = Foreground(90)
        val LightRed = Foreground(91)
        val LightGreen = Foreground(92)
        val DarkYellow = Foreground(93)
        val LightBlue = Foreground(94)
        val LightMagenta = Foreground(95)
        val LightCyan = Foreground(96)
        val White = Foreground(97)
    }
}