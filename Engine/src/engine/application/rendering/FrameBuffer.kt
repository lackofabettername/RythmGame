package engine.application.rendering

import engine.console.logging.Log
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL30C.*
import java.nio.ByteBuffer

class FrameBuffer(
    val Width: Int,
    val Height: Int,
) {
    val ID = glGenFramebuffers()

    val colorTexture by lazy { Texture(TextureType.Texture2D) }
    val depthTexture by lazy { Texture(TextureType.Texture2D) }

    fun attachColorBuffer() {
        attachBuffer(colorTexture.ID, GL_RGB, GL_COLOR_ATTACHMENT0)
    }

    fun attachDepthBuffer() {
        attachBuffer(depthTexture.ID, GL_DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT)
    }

    //TODO: different buffer types, stencil and depth. Multiple color buffers?
    private fun attachBuffer(texture: Int, format: Int, attachmentType: Int) {
        bind()

        // generate texture
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            format,
            Width,
            Height,
            0,
            format,
            GL_UNSIGNED_BYTE,
            null as ByteBuffer?
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glBindTexture(GL_TEXTURE_2D, 0)

        // attach it to currently bound framebuffer object
        glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            attachmentType,
            GL_TEXTURE_2D,
            texture,
            0
        )

        //unbind()
    }

    fun verify() {
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) {
            Log.debug("FrameBuffer complete.")
        } else {
            Log.warn("FrameBuffer not complete!")
        }
    }

    fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, ID)
    }

    fun unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun cleanup() {
        glDeleteFramebuffers(ID)
    }

    fun setClearColor(r: Float, g: Float, b: Float, alpha: Float) {
        glClearColor(r, g, b, alpha)
    }

    fun clear(mask: Int) {
        GL11C.glClear(mask)
    }

    companion object {
        val ColorBuffer = GL_COLOR_BUFFER_BIT
        val DepthBuffer = GL_DEPTH_BUFFER_BIT
        val StencilBuffer = GL_STENCIL_BUFFER_BIT
    }
}