package util.misc

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.*

//https://www.baeldung.com/kotlin/byte-arrays-to-hex-strings
fun ByteArray.toHexString() = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

object Checksum {
    fun digest(file: File): ByteArray {
        if (!file.exists())
            return ByteArray(16)

        val md = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(1024)

        val queue = LinkedList(listOf(file))
        while (queue.isNotEmpty()) {
            val f = queue.poll()
            if (f.exists()) {
                if (f.isDirectory) {
                    for (child in f.listFiles())
                        queue.add(child)
                } else {
                    //Log.trace("Checksum", "$f")

                    val fis: InputStream = FileInputStream(f)
                    var numRead: Int

                    do {
                        numRead = fis.read(buffer)
                        if (numRead > 0) {
                            md.update(buffer, 0, numRead)
                        }
                    } while (numRead != -1)
                }
            }
        }

        return md.digest() ?: ByteArray(16)
    }
}