package engine.application.rendering

import engine.console.logging.Log
import engine.files.FileAccess
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL13C.*
import org.lwjgl.opengl.GL30C.GL_TEXTURE_3D
import org.lwjgl.opengl.GL30C.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import java.nio.ByteBuffer
import kotlin.math.max

class Texture(
    val Type: TextureType
) {
    val ID = glGenTextures()

    fun bind(textureUnit: Int) {
        glActiveTexture(GL_TEXTURE0 + textureUnit)
        bind()
    }

    fun bind() {
        glBindTexture(Type._id, ID)
    }

    fun unbind() {
        glBindTexture(Type._id, 0)
    }

    fun cleanup() {
        glDeleteTextures(ID)
    }

    companion object {
        private fun loadImg(file: FileAccess): Triple<ByteBuffer?, Int, Int> {
            val width = BufferUtils.createIntBuffer(1)
            val height = BufferUtils.createIntBuffer(1)
            val comp = BufferUtils.createIntBuffer(1)

            val size = file.Channel.size()
            require(size <= Int.MAX_VALUE) { "TODO: larger files" }
            val rawData = BufferUtils.createByteBuffer(size.toInt())
            file.Channel.read(rawData)
            rawData.flip()

            //Log.trace("Texture", raw.contentToString())

            val data = stbi_load_from_memory(rawData, width, height, comp, 4)
            if (data == null)
                Log.error(stbi_failure_reason().toString())
            else {
                //Log.trace("Texture", "$data")
                //Log.trace("Texture", data.contentToString())
                Log.trace("Texture", "${width[0]}, ${height[0]}, ${comp[0]}")
            }

            return Triple(data, width[0], height[0])
        }

        fun load2D(file: FileAccess): Texture {
            val (imageData, width, height) = loadImg(file)


            val texture = Texture(TextureType.Texture2D)
            texture.bind()
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)

            glPixelStorei(GL_BLEND, GL_LINEAR)
            glTexParameteri(TextureType.Texture2D._id, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)

            glTexImage2D(TextureType.Texture2D._id, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData)
            glGenerateMipmap(TextureType.Texture2D._id)

            stbi_image_free(imageData)

            return texture
        }

        fun load1D(file: FileAccess): Texture {
            val (imageData, width, height) = loadImg(file)
            val size = max(width, height)


            val texture = Texture(TextureType.Texture1D)
            texture.bind()
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)

            glPixelStorei(GL_BLEND, GL_LINEAR)

            glTexImage1D(TextureType.Texture1D._id, 0, GL_RGBA, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData)
            glGenerateMipmap(TextureType.Texture1D._id)

            stbi_image_free(imageData)

            return texture
        }
    }
}

enum class TextureType(
    internal val _id: Int
) {
    Texture1D(GL_TEXTURE_1D),
    Texture2D(GL_TEXTURE_2D),
    Texture3D(GL_TEXTURE_3D),
}