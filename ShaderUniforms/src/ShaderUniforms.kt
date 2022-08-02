import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.relativeTo

//TODO: cleanup
class ShaderUniforms {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val srcFileName = args[0]
            val extension = args[1]

            val srcFile = File("$srcFileName.$extension")
            if (!srcFile.exists()) return

            val genFileName = "${srcFileName}Shader"
            val root = File(args[2]).toPath()
            val resources = "$root/src/main/resources"
            val shaders = "$root/src/main/shaders"
            val path = Path(genFileName)
                .toAbsolutePath()
                .relativeTo(File(resources).toPath())
                .toString()
                .replace("\\", "/")//Fuck windows
            val `package` = path.replace("\\", "/")//Fuck windows
                .removeSuffix("/$genFileName")
                .replace('/', '.')

            println(path)
            println(`package`)

            val genFile = File("$shaders/$path.kt")
            println("Generating file: ${genFile.absolutePath}")

            if (!genFile.exists()) {
                genFile.toPath().parent.createDirectories()
                genFile.createNewFile()
            }

            val oldUniforms = genFile.useLines { lines ->
                lines.map { Regex("\\s*val (.+) = .*//(.+)").find(it) }
                    .filterNotNull()
                    .associate { it.groupValues[1] to it.groupValues[2].trim().split(" ").toMutableSet() }
                    .toMutableMap()
            }

            println("Old uniforms: $oldUniforms")

            val uniforms = srcFile.useLines { lines ->
                lines.map { Regex("^\\s*uniform(?:\\s+\\S+)*\\s+(\\S+?)(?:;|\$|\\s)").find(it) }
                    .map { it?.groupValues?.getOrNull(1) }
                    .filterNotNull()
                    .toList()
            }

            oldUniforms.forEach { uniform, source ->
                if (uniform in uniforms)
                    source += extension
                else
                    source -= extension
            }
            uniforms.forEach { uniform ->
                if (uniform !in oldUniforms) {
                    oldUniforms[uniform] = mutableSetOf(extension)
                }
            }

            println("New uniforms: $oldUniforms")


            val writer = genFile.printWriter()
            writer.println("//Auto generated from src/main/resources/$path")
            if (`package`.isNotEmpty()) {
                writer.println()
                writer.println("package $`package`")
            }
            writer.println()
            writer.println("object $genFileName {")
            writer.println("\tval Path = \"${path.removeSuffix("Shader")}\"")
            oldUniforms.forEach { (uniform, source) ->
                if (source.isEmpty()) return@forEach

                writer.print("\tval $uniform = \"$uniform\"//")
                for (extension in source)
                    writer.print("$extension ")
                writer.println()
            }
            writer.println("}")
            writer.close()
        }
    }
}