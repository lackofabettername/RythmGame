package engine.application.events

import org.lwjgl.glfw.GLFW

/**
 * @param Key       The ascii character of the pressed key(?) (need to verify)
 *
 * @param Scancode  The scancode is unique for every key, regardless of whether it has a key token.
 * Scancodes are platform-specific but consistent over time,
 * so keys will have different scancodes depending on the platform but they are safe to save to disk.
 * You can query the scancode for any named key on the current platform with [glfwGetKeyScancode][org.lwjgl.glfw.GLFW.glfwGetKeyScancode].
 *
 * @param Modifiers The modifiers that were active when the key was pressed: shift, ctrl, alt, etc
 * @see <a href="https://www.glfw.org/docs/3.3/input_guide.html#input_key">glfw docs for keyinput</a>
 */
data class Key(val Key: Int, val Scancode: Int, val Modifiers: Modifiers) {

    override fun equals(other: Any?): Boolean {
        return other is Key && other.Scancode == Scancode
    }

    override fun hashCode(): Int {
        return Scancode
    }

    companion object {
        internal fun initialize() {
            val convert: (Int) -> Key = { key -> Key(key, GLFW.glfwGetKeyScancode(key), Modifiers(0))}

            A = convert(GLFW.GLFW_KEY_A)
            B = convert(GLFW.GLFW_KEY_B)
            C = convert(GLFW.GLFW_KEY_C)
            D = convert(GLFW.GLFW_KEY_D)
            E = convert(GLFW.GLFW_KEY_E)
            F = convert(GLFW.GLFW_KEY_F)
            G = convert(GLFW.GLFW_KEY_G)
            H = convert(GLFW.GLFW_KEY_H)
            I = convert(GLFW.GLFW_KEY_I)
            J = convert(GLFW.GLFW_KEY_J)
            K = convert(GLFW.GLFW_KEY_K)
            L = convert(GLFW.GLFW_KEY_L)
            M = convert(GLFW.GLFW_KEY_M)
            N = convert(GLFW.GLFW_KEY_N)
            O = convert(GLFW.GLFW_KEY_O)
            P = convert(GLFW.GLFW_KEY_P)
            Q = convert(GLFW.GLFW_KEY_Q)
            R = convert(GLFW.GLFW_KEY_R)
            S = convert(GLFW.GLFW_KEY_S)
            T = convert(GLFW.GLFW_KEY_T)
            U = convert(GLFW.GLFW_KEY_U)
            V = convert(GLFW.GLFW_KEY_V)
            W = convert(GLFW.GLFW_KEY_W)
            X = convert(GLFW.GLFW_KEY_X)
            Y = convert(GLFW.GLFW_KEY_Y)
            Z = convert(GLFW.GLFW_KEY_Z)

            Space = convert(GLFW.GLFW_KEY_SPACE)
            LShift = convert(GLFW.GLFW_KEY_LEFT_SHIFT)

            Escape = convert(GLFW.GLFW_KEY_ESCAPE)
        }

        lateinit var A: Key
        lateinit var B: Key
        lateinit var C: Key
        lateinit var D: Key
        lateinit var E: Key
        lateinit var F: Key
        lateinit var G: Key
        lateinit var H: Key
        lateinit var I: Key
        lateinit var J: Key
        lateinit var K: Key
        lateinit var L: Key
        lateinit var M: Key
        lateinit var N: Key
        lateinit var O: Key
        lateinit var P: Key
        lateinit var Q: Key
        lateinit var R: Key
        lateinit var S: Key
        lateinit var T: Key
        lateinit var U: Key
        lateinit var V: Key
        lateinit var W: Key
        lateinit var X: Key
        lateinit var Y: Key
        lateinit var Z: Key

        lateinit var Space: Key
        lateinit var LShift: Key

        lateinit var Escape: Key
    }
}