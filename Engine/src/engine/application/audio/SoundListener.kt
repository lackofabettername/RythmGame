package engine.application.audio

import org.lwjgl.openal.AL10.*
import util.Vector3

class SoundListener @JvmOverloads constructor(position: Vector3 = Vector3()) {
    init {
        alListener3f(AL_POSITION, position.x, position.y, position.z)
        alListener3f(AL_VELOCITY, 0f, 0f, 0f)
    }

    var speed: Vector3
        set(speed) {
            alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z)
        }
        get() {
            TODO()
        }

    var position: Vector3
        set(position) {
            alListener3f(AL_VELOCITY, position.x, position.y, position.z)
        }
        get() {
            TODO()
        }

    fun setOrientation(at: Vector3, up: Vector3) {
        alListenerfv(AL_ORIENTATION, at.floatArray + up.floatArray)
    }
}