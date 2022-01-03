package engine.application.rendering

import logging.Log
import org.lwjgl.opengl.GL20C.*

import util.Vector2
import util.Vector3

class Shader {
    val ID = glCreateProgram()

    init {
        check(ID != 0) { "Could not create ShaderProgram" }
    }

    var vertID = 0
        private set

    var geomID = 0
        private set

    var fragID = 0
        private set

    private val uniforms = HashMap<String, Int>()

    //region Constructor
    fun createVertexShader(fileName: String) {
        //val shaderCode = FileSystem.readLine(fileName)
        //val shaderCode = Utils.loadResource(fileName + if (fileName.contains(".")) "" else ".vert")
        //Log.trace("ShaderProgram", "\n$shaderCode")
        //vertID = createShader(shaderCode, GL_VERTEX_SHADER)
    }

    fun createGeometryShader(fileName: String) {
        TODO("Load File")
        //val shaderCode = Utils.loadResource(fileName + if (fileName.contains(".")) "" else ".geom")
        //Log.trace("ShaderProgram", "\n$shaderCode")
        //geomID = createShader(shaderCode, GL_GEOMETRY_SHADER)
    }

    fun createFragmentShader(fileName: String) {
        TODO("Load File")
        //val shaderCode = Utils.loadResource(fileName + if (fileName.contains(".")) "" else ".frag")
        //Log.trace("ShaderProgram", "\n$shaderCode")
        //fragID = createShader(shaderCode, GL_FRAGMENT_SHADER)
    }

    private fun createShader(shaderCode: String, shaderType: Int): Int {
        val shaderID = glCreateShader(shaderType)
        check(shaderID != 0) { "Error creating shader. Type: $shaderType" }

        glShaderSource(shaderID, shaderCode)
        glCompileShader(shaderID)

        check(glGetShaderi(shaderID, GL_COMPILE_STATUS) != 0) {
            "Error compiling Shader code: ${
                glGetShaderInfoLog(shaderID, 1024)
            }"
        }

        glAttachShader(ID, shaderID)

        return shaderID
    }
    //endregion

    fun createUniform(uniform: String) {
        val uniformLocation = glGetUniformLocation(ID, uniform)

        require(uniformLocation >= 0) { "Could not find uniform: $uniform" }

        uniforms[uniform] = uniformLocation
    }

    fun createUniforms(vararg uniforms: String) {
        for (uniform in uniforms) {
            createUniform(uniform)
        }
    }

    fun setUniform(uniform: String, value: Int) {
        glUniform1i(getUniform(uniform), value)
    }

    fun setUniform(uniform: String, value: Float) {
        glUniform1f(getUniform(uniform), value)
    }

    fun setUniform(uniform: String, value: Vector2) {
        glUniform2f(getUniform(uniform), value.x, value.y)
    }

    fun setUniform(uniform: String, value: Vector3) {
        glUniform3f(getUniform(uniform), value.x, value.y, value.z)
    }

    private fun getUniform(uniform: String): Int {
        val uniform = uniforms[uniform]
        require(uniform != null) { "Could not find uniform $uniform" }
        return uniform
    }

    fun link() {
        glLinkProgram(ID)

        check(glGetProgrami(ID, GL_LINK_STATUS) != 0) {
            "Error linking Shader code: ${
                glGetProgramInfoLog(ID, 1024)
            }"
        }

        if (vertID != 0) {
            glDetachShader(ID, vertID)
            glDeleteProgram(vertID)
            vertID = 0
        }

        if (geomID != 0) {
            glDetachShader(ID, geomID)
            glDeleteProgram(geomID)
            geomID = 0
        }

        if (fragID != 0) {
            glDetachShader(ID, fragID)
            glDeleteProgram(fragID)
            fragID = 0
        }

        glValidateProgram(ID)
        if (glGetProgrami(ID, GL_VALIDATE_STATUS) == 0) {
            Log.warn("ShaderProgram", "Warning validating Shader code: " + glGetProgramInfoLog(ID, 1024))
        }
    }

    fun bind() {
        glUseProgram(ID)
    }

    fun cleanup() {
        unbind()

        if (ID != 0) {
            glDeleteProgram(ID)
        }
    }

    companion object {
        fun unbind() {
            glUseProgram(0)
        }
    }
}