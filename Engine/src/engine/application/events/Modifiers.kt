package engine.application.events

import org.lwjgl.glfw.GLFW.*

// @formatter:off
data class Modifiers(val mods: Int) {
    val shift    get() = mods and GLFW_MOD_SHIFT     > 0
    val control  get() = mods and GLFW_MOD_CONTROL   > 0
    val alt      get() = mods and GLFW_MOD_ALT       > 0
    val `super`  get() = mods and GLFW_MOD_SUPER     > 0
    val capsLock get() = mods and GLFW_MOD_CAPS_LOCK > 0
    val numLock  get() = mods and GLFW_MOD_NUM_LOCK  > 0
}
