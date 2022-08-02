package engine.application.rendering

import Color
import engine.console.logging.Log
import engine.files.FileAccessMode
import engine.files.FileSystem
import misc.use
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import util.Matrix3x3
import util.Matrix4x4

import util.Vector2
import util.Vector3

//If uniforms aren't being set, you may have forgotten to bind the shader
class Shader {
    val ID = glCreateProgram()
        .also {
            require(it != 0) { "Could not create ShaderProgram" }
        }

    var VertID = 0
        private set

    var GeomID = 0
        private set

    var FragID = 0
        private set

    val uniforms = Uniforms()

    //region Constructor
    fun createVertexShader(fileName: String) {
        FileSystem.openResource(
            fileName + if (fileName.contains(".")) "" else ".vert",
            FileAccessMode.Read
        )?.use {
            val shaderCode = it.Reader.lineSequence().joinToString("\n")
            VertID = createShader(shaderCode, GL_VERTEX_SHADER)
        }
    }

    fun createGeometryShader(fileName: String) {
        FileSystem.openResource(
            fileName + if (fileName.contains(".")) "" else ".geom",
            FileAccessMode.Read
        )?.use {
            val shaderCode = it.Reader.lineSequence().joinToString("\n")
            VertID = createShader(shaderCode, GL_GEOMETRY_SHADER)
        }
    }

    fun createFragmentShader(fileName: String) {
        FileSystem.openResource(
            fileName + if (fileName.contains(".")) "" else ".frag",
            FileAccessMode.Read
        )?.use {
            val shaderCode = it.Reader.lineSequence().joinToString("\n")
            VertID = createShader(shaderCode, GL_FRAGMENT_SHADER)
        }
    }

    private fun createShader(shaderCode: String, shaderType: Int): Int {
        Log.trace(
            "ShaderProgram",
            "Creating a ${_typeNameMap[shaderType]} shader...\n$shaderCode"
        )

        val shaderID = glCreateShader(shaderType)
        check(shaderID != 0) { "Error creating shader. Type: ${_typeNameMap[shaderType]}" }

        glShaderSource(shaderID, shaderCode)
        glCompileShader(shaderID)

        check(glGetShaderi(shaderID, GL_COMPILE_STATUS) != 0) {
            "Error compiling ${_typeNameMap[shaderType]} Shader code:\n${
                glGetShaderInfoLog(shaderID, 1024)
            }"
        }

        glAttachShader(ID, shaderID)

        return shaderID
    }
    //endregion

    fun link() {
        glLinkProgram(ID)

        check(glGetProgrami(ID, GL_LINK_STATUS) != 0) {
            "Error linking Shader code: ${
                glGetProgramInfoLog(ID, 1024)
            }"
        }

        if (VertID != 0) {
            glDetachShader(ID, VertID)
            glDeleteProgram(VertID)
            VertID = 0
        }

        if (GeomID != 0) {
            glDetachShader(ID, GeomID)
            glDeleteProgram(GeomID)
            GeomID = 0
        }

        if (FragID != 0) {
            glDetachShader(ID, FragID)
            glDeleteProgram(FragID)
            FragID = 0
        }

        glValidateProgram(ID)
        if (glGetProgrami(ID, GL_VALIDATE_STATUS) == 0) {
            Log.warn("ShaderProgram", "Warning validating Shader code: " + glGetProgramInfoLog(ID))
        }
    }

    fun bind() {
        glUseProgram(ID)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun cleanup() {
        unbind()

        if (ID != 0) {
            glDeleteProgram(ID)
        }
    }

    inner class Uniforms internal constructor() {
        private val _keys = HashMap<String, Int>()

        operator fun plusAssign(uniform: String) {
            val uniformLocation = glGetUniformLocation(ID, uniform)

            require(uniformLocation >= 0) { "Could not create uniform \"$uniform\"" }

            _keys[uniform] = uniformLocation
        }

        operator fun set(uniform: String, value: Int) {
            glUniform1i(getUniform(uniform), value)
        }

        operator fun set(uniform: String, value: Float) {
            glUniform1f(getUniform(uniform), value)
        }

        operator fun set(uniform: String, value: Vector2) {
            glUniform2f(getUniform(uniform), value.x, value.y)
        }

        operator fun set(uniform: String, value: Vector3) {
            glUniform3f(getUniform(uniform), value.x, value.y, value.z)
        }

        operator fun set(uniform: String, value: Matrix3x3) {
            glUniformMatrix3fv(getUniform(uniform), true, value.values)
        }

        operator fun set(uniform: String, value: Matrix4x4) {
            glUniformMatrix4fv(getUniform(uniform), true, value.values)
        }

        operator fun set(uniform: String, color: Color) {
            glUniform3f(getUniform(uniform), color.red, color.green, color.blue)
        }

        private fun getUniform(uniform: String) =
            _keys[uniform] ?: error("Could not find uniform $uniform")
    }

    companion object {
        private val _typeNameMap = mapOf(
            GL_VERTEX_SHADER to "Vertex",
            GL_GEOMETRY_SHADER to "Geometry",
            GL_FRAGMENT_SHADER to "Fragment",
        )

        fun unbind() {
            glUseProgram(0)
        }
    }
}