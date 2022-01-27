package engine.sortMe

import engine.console.logging.Log
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.libc.LibCStdlib.free
import java.nio.ShortBuffer
import kotlin.math.sin

object SoundTest {
    var device = 0L; private set
    var context = 0L; private set

    fun initialize() {
        //Initialization
        val defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER)
        device = alcOpenDevice(defaultDeviceName)

        val attributes = intArrayOf(0)
        context = alcCreateContext(device, attributes)
        alcMakeContextCurrent(context)

        val alcCapabilities = ALC.createCapabilities(device)
        val alCapabilities = AL.createCapabilities(alcCapabilities)
    }

    fun cleanup() {
        //Terminate OpenAL
        alcDestroyContext(context)
        alcCloseDevice(device)
    }
}

fun main() {
    SoundTest.initialize()

    var rawAudioBuffer: ShortBuffer?

    var channels: Int
    var sampleRate: Int

    stackPush().use { stack ->
        //Allocate space to store return information from the function
        val channelsBuffer = stack.mallocInt(1)
        val sampleRateBuffer = stack.mallocInt(1)
        rawAudioBuffer = stb_vorbis_decode_filename(
            "C:\\Users\\Adrian\\Downloads\\DANCE WITH THE DEAD - Riot.ogg",
            channelsBuffer,
            sampleRateBuffer
        )

        //Retreive the extra information that was stored in the buffers by the function
        channels = channelsBuffer[0]
        sampleRate = sampleRateBuffer[0]
    }

    //Find the correct OpenAL format
    var format = -1
    if (channels == 1) {
        format = AL_FORMAT_MONO16
    } else if (channels == 2) {
        format = AL_FORMAT_STEREO16
    }

    //Request space for the buffer
    val bufferPointer = alGenBuffers()

    //Send the data to OpenAL
    alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate)

    //Free the memory allocated by STB
    free(rawAudioBuffer)

    //Request a source
    val sourcePointer = alGenSources()

    //Assign the sound we just loaded to the source
    alSourcei(sourcePointer, AL_BUFFER, bufferPointer)

    //Play the sound
    alSourcePlay(sourcePointer)

    alSourcei(sourcePointer, AL_SOURCE_RELATIVE, AL_TRUE)
    try {
        for (i in 0 until 10000) {
            val v = sin(i * 0.01f) * 100
            Log.debug(v.toString())
            //alSourcef(sourcePointer, AL_GAIN, v)
            alSource3f(sourcePointer, AL_POSITION, 0f, 0f, v)
            Thread.sleep(10)
        }
    } catch (ignored: InterruptedException) {
    }

    alDeleteSources(sourcePointer)
    alDeleteBuffers(bufferPointer)

    SoundTest.cleanup()
}