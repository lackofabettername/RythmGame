package engine.console.logging.style

/**
 * @see Foreground
 *
 * @see Background
 *
 * @see Font
 */
open class Style(value: Int) {
    private val code: String = "${27.toChar()}[${value}m"

    override fun toString() = code

    companion object {
        @JvmField
        val Clear = Style(0)
        val Reset = Style(2) //Is this number reserved for anything?
    }
}