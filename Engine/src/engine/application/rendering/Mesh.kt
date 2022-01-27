package engine.application.rendering

import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL15C
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.system.MemoryUtil
import util.Vector
import java.lang.Float
import java.nio.FloatBuffer
import kotlin.Array
import kotlin.FloatArray
import kotlin.Int
import kotlin.IntArray
import kotlin.let
import kotlin.require


//todo: indexbuffer
class Mesh(
    val PrimitiveType: Int,
    val RenderType: Int = StaticDraw,
    val Indices: IntArray?,
    vararg val VertexData: Array<Vector>
) {

    constructor(
        type: Int,
        renderType: Int = StaticDraw,
        vararg vertexData: Array<Vector>,
    ) : this(type, renderType, null, *vertexData)

    private val rawVertexData: FloatArray

    val VertexCount: Int
    val VertexSize: Int
    val ComponentCount: Int

    val VaoID: Int
    val VboID: Int
    val indicesBuffer: Int

    /*
    vertexData = [
                  [[x, y, z], [x, y, z], [x, y, z]]
                  [[a, b],    [a, b],    [a, b]   ]
                  [[1, 2, 3], [1, 2, 3], [1, 2, 3]]
                  [[val],     [val],     [val]    ]
                                                     ]
     */
    init {
        ComponentCount = VertexData.size
        VertexCount = VertexData[0].size
        VertexSize = VertexData.fold(0) { acum, elem -> acum + elem[0].getDimension() }

        rawVertexData = FloatArray(VertexCount * VertexSize)
        var ind = 0
        for (vertex in 0 until VertexCount) {
            for (component in 0 until ComponentCount) {
                for (element in 0 until VertexData[component][0].getDimension()) {
                    rawVertexData[ind++] = VertexData[component][vertex][element]
                }
            }
        }

        VaoID = glGenVertexArrays()
        glBindVertexArray(VaoID)

        VboID = glGenBuffers()
        var vbo: FloatBuffer? = null

        indicesBuffer = glGenBuffers()
        try {
            //Vertex data
            glBindBuffer(GL_ARRAY_BUFFER, VboID)
            glBufferData(GL_ARRAY_BUFFER, rawVertexData, RenderType)

            ind = 0
            for (i in 0 until ComponentCount) {
                val componentSize: Int = VertexData[i][0].getDimension()

                glEnableVertexAttribArray(i)
                glVertexAttribPointer(
                    i,
                    componentSize,
                    GL_FLOAT,
                    false,
                    VertexSize * Float.BYTES,
                    ind.toLong() * Float.BYTES
                )
                ind += componentSize
            }

            Indices?.let {
                //Indices
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer)
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, it, GL_STATIC_DRAW)
            }
        } finally {
            if (vbo != null) {
                MemoryUtil.memFree(vbo)
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    fun updateVertex(vertex: Int, vararg value: Vector) {
        require(value.size == ComponentCount)

        val offset = vertex * VertexSize
        var ind = offset

        for (component in 0 until ComponentCount) {
            for (element in 0 until VertexData[component][0].getDimension()) {
                rawVertexData[ind++] = value[component][element]
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, VboID)
        GL15C.glBufferSubData(
            GL_ARRAY_BUFFER,
            (offset * Float.BYTES).toLong(),
            rawVertexData.sliceArray(offset until ind)
        )
    }

    fun cleanup() {
        glBindVertexArray(VaoID)

        for (i in 0 until ComponentCount) {
            glDisableVertexAttribArray(i)
        }

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(VboID)

        // Delete the VAO
        glBindVertexArray(0)
        glDeleteVertexArrays(VaoID)
    }

    fun render(shader: Shader) {
        shader.bind()

        // Draw the mesh
        if (Indices == null) {
            glBindVertexArray(VaoID)
            glDrawArrays(PrimitiveType, 0, VertexCount)
        } else {
            glBindVertexArray(VaoID)
            GL11C.glDrawElements(
                PrimitiveType,
                Indices.size,
                GL_UNSIGNED_INT,
                0
            )
        }

        // Restore state
        //glBindVertexArray(0);
        //glBindTexture(GL_TEXTURE_2D, 0);

        //shader.unbind();
    }

    companion object {
        //@formatter:off
        const val Points        = GL_POINTS
        const val Lines         = GL_LINES
        const val LineLoop      = GL_LINE_LOOP
        const val LineStrip     = GL_LINE_STRIP
        const val Triangles     = GL_TRIANGLES
        const val TriangleStrip = GL_TRIANGLE_STRIP
        const val TriangleFan   = GL_TRIANGLE_FAN

        const val StaticDraw = GL_STATIC_DRAW
        const val DynamicDraw = GL_DYNAMIC_DRAW
        //@formatter:on
    }
}