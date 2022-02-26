package engine.application.audio

import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.alDistanceModel
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer

object SoundManager {
    private val device = alcOpenDevice(null as ByteBuffer?)
    private val context: Long
    var listener: SoundListener

    private val soundBufferList = ArrayList<SoundBuffer>()
    private val soundSourceMap = HashMap<String, SoundSource>()

    init {
        check(device != MemoryUtil.NULL) { "Failed to open the default OpenAL device." }
        val deviceCaps = ALC.createCapabilities(device)
        context = alcCreateContext(device, null as IntBuffer?)
        check(context != MemoryUtil.NULL) { "Failed to create OpenAL context." }
        alcMakeContextCurrent(context)
        AL.createCapabilities(deviceCaps)

        listener = SoundListener()
    }

    fun addSoundSource(name: String, soundSource: SoundSource) {
        soundSourceMap[name] = soundSource
    }

    fun getSoundSource(name: String): SoundSource? {
        return soundSourceMap[name]
    }

    fun playSoundSource(name: String) {
        val soundSource: SoundSource? = soundSourceMap[name]
        if (soundSource != null && !soundSource.isPlaying) {
            soundSource.play()
        }
    }

    fun removeSoundSource(name: String) {
        soundSourceMap.remove(name)
    }

    fun addSoundBuffer(soundBuffer: SoundBuffer) {
        soundBufferList.add(soundBuffer)
    }

    //fun updateListenerPosition(camera: Camera) {
    //    // Update camera matrix with camera data
    //    Transformation.updateGenericViewMatrix(camera.getPosition(), camera.getRotation(), cameraMatrix)
    //    listener!!.setPosition(camera.getPosition())
    //    val at = Vector3f()
    //    cameraMatrix.positiveZ(at).negate()
    //    val up = Vector3f()
    //    cameraMatrix.positiveY(up)
    //    listener!!.setOrientation(at, up)
    //}

    fun setAttenuationModel(model: Int) {
        alDistanceModel(model)
    }

    fun cleanup() {
        for (soundSource in soundSourceMap.values) {
            soundSource.cleanup()
        }
        soundSourceMap.clear()
        for (soundBuffer in soundBufferList) {
            soundBuffer.cleanup()
        }
        soundBufferList.clear()
        if (context != MemoryUtil.NULL) {
            alcDestroyContext(context)
        }
        if (device != MemoryUtil.NULL) {
            alcCloseDevice(device)
        }
    }
}

fun main() {
    val manager = SoundManager

    val buffer =
        SoundBuffer("C:\\Users\\Adrian\\Downloads\\Trial of Thunder (5-3) - Crypt of the Necrodancer Metal Soundtrack FamilyJules.ogg")
    val source = SoundSource(false, false)
    source.setBuffer(buffer.bufferId)

    manager.addSoundBuffer(buffer)
    manager.addSoundSource("sourceTest", source)
    manager.listener = SoundListener()

    source.gain = 0.4f
    source.play()

    val end = System.nanoTime() + 60 * 3 * 1_000_000_000L
    var prevSecond = -1f
    var prevSample = -1
    while (System.nanoTime() < end) {
        val seconds = source.seconds
        val samples = source.samples

        if (seconds != prevSecond || samples != prevSample) {
            prevSecond = seconds
            prevSample = samples
            println("seconds: ${source.seconds} - samples: ${source.samples}")
        }
        Thread.onSpinWait()
    }

    manager.cleanup()
}