package engine.application.audio

import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.AL11.AL_SAMPLE_OFFSET
import org.lwjgl.openal.AL11.AL_SEC_OFFSET
import space.Vector3
import javax.naming.OperationNotSupportedException

class SoundSource(loop: Boolean, relative: Boolean) {
    private val _ID = alGenSources()

    val seconds get() = alGetSourcef(_ID, AL_SEC_OFFSET)
    val samples get() = alGetSourcei(_ID, AL_SAMPLE_OFFSET)

    init {
        if (loop)
            alSourcei(_ID, AL_LOOPING, AL_TRUE)

        if (relative)
            alSourcei(_ID, AL_SOURCE_RELATIVE, AL_TRUE)
    }

    fun setBuffer(bufferId: Int) {
        stop()
        alSourcei(_ID, AL_BUFFER, bufferId)
    }

    var position: Vector3
        set(position: Vector3) {
            alSource3f(_ID, AL_POSITION, position.x, position.y, position.z)
        }
        get() {
            val a = FloatArray(1)
            val b = FloatArray(1)
            val c = FloatArray(1)
            alGetSource3f(_ID, AL_POSITION, a, b, c)
            return Vector3(a[0], b[0], c[0])
        }

    var speed: Vector3
        set(speed: Vector3) {
            alSource3f(_ID, AL_POSITION, position.x, position.y, position.z)
        }
        get() {
            throw OperationNotSupportedException()
        }

    var gain: Float
        set(gain: Float) {
            alSourcef(_ID, AL_GAIN, gain)
        }
        get() = alGetSourcef(_ID, AL_GAIN)

    fun setProperty(param: Int, value: Float) {
        alSourcef(_ID, param, value)
    }

    fun play() {
        alSourcePlay(_ID)
    }

    val isPlaying get() = alGetSourcei(_ID, AL_SOURCE_STATE) == AL_PLAYING

    fun pause() {
        alSourcePause(_ID)
    }

    fun stop() {
        alSourceStop(_ID)
    }

    fun cleanup() {
        stop()
        alDeleteSources(_ID)
    }
}