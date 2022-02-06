package engine.application.rendering

import engine.console.logging.Log
import engine.files.FileAccess
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL30C.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import java.nio.ByteBuffer

fun ByteBuffer.contentToString() = (0 until this.limit())
    .mapIndexed { i, _ ->
        this[i].toUByte().toString(16)
            .padStart(2, '0')
    }.toString()
    .replace(",", "")


class Texture(file: FileAccess) {
    val ID: Int

    init {
        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val comp = BufferUtils.createIntBuffer(1)

        val size = file.Channel.size()
        require(size <= Int.MAX_VALUE) { "TODO: larger files" }
        val raw = BufferUtils.createByteBuffer(size.toInt())
        file.Channel.read(raw)
        raw.flip()

        Log.trace("Texture", raw.contentToString())

        val data = stbi_load_from_memory(raw, width, height, comp, 4)
        if (data == null)
            Log.error(stbi_failure_reason().toString())
        else {
            Log.trace("Texture", "$data")
            Log.trace("Texture", data.contentToString())
            Log.trace("Texture", "${width[0]}, ${height[0]}, ${comp[0]}")
        }

        ID = glGenTextures()
        bind()
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)

        glPixelStorei(GL_BLEND, GL_LINEAR)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width[0], height[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
        glGenerateMipmap(GL_TEXTURE_2D)

        stbi_image_free(data)
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, ID)
    }

    fun cleanup() {
        glDeleteTextures(ID)
    }
}