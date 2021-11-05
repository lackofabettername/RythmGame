package engine.application.events

/**
 * @param Key       The ascii character of the pressed key(?) (need to verify)
 *
 *
 * @param Scancode  The scancode is unique for every key, regardless of whether it has a key token.
 * Scancodes are platform-specific but consistent over time,
 * so keys will have different scancodes depending on the platform but they are safe to save to disk.
 * You can query the scancode for any named key on the current platform with [glfwGetKeyScancode][org.lwjgl.glfw.GLFW.glfwGetKeyScancode].
 *
 *
 * @param Modifiers The modifiers that were active when the key was pressed: shift, ctrl, alt, etc
 * @see <a href="https://www.glfw.org/docs/3.3/input_guide.html#input_key">glfw docs for keyinput</a>
 */
data class Key(val Key: Int, val Scancode: Int, val Modifiers: Int) {

}