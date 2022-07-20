package engine.application.audio

import engine.files.FileAccessMode
import engine.files.FileSystem
import misc.use
import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL10.*
import org.lwjgl.stb.STBVorbis
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.ShortBuffer

class SoundBuffer(file: String) {
    val bufferId: Int = alGenBuffers()
    private var pcm: ShortBuffer? = null
    private var vorbis: ByteBuffer? = null

    init {
        STBVorbisInfo.malloc().use { info ->
            MemoryStack.stackPush().use { stack ->
                FileSystem.openFile(file, FileAccessMode.Read)!!.use { file ->
                    vorbis = BufferUtils.createByteBuffer(file.Channel.size().toInt())
                    file.Channel.read(vorbis)
                    vorbis!!.flip()
                }

                val error = stack.mallocInt(1)
                val decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null)

                require(decoder != MemoryUtil.NULL) { "Failed to open Ogg Vorbis file. Error: ${error[0]}" }

                STBVorbis.stb_vorbis_get_info(decoder, info)

                val channels = info.channels()
                val lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder)

                pcm = MemoryUtil.memAllocShort(lengthSamples)
                pcm!!.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels)

                STBVorbis.stb_vorbis_close(decoder)
            }


            // Copy to buffer
            alBufferData(
                bufferId,
                if (info.channels() == 1) AL_FORMAT_MONO16 else AL_FORMAT_STEREO16,
                pcm,
                info.sample_rate()
            )
        }
    }

    fun cleanup() {
        alDeleteBuffers(bufferId)
        MemoryUtil.memFree(pcm)
    }
}