package logging.style

@Suppress("unused")
class Font(value: Int) : Style(value) {

    companion object {
        val Bold = Font(1)
        val Italics = Font(3)
        val Underline = Font(4)
        val StrikeThrough = Font(9)
        val Framed = Font(51)
    }

}