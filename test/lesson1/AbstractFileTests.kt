package lesson1

import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*
import java.util.zip.CRC32
import kotlin.math.max
import kotlin.test.assertEquals

abstract class AbstractFileTests {
    protected fun assertFileContent(name: String, expectedContent: String) {
        val content = File(name).readLines().joinToString("\n")
        assertEquals(expectedContent.trim(), content.trim())
    }

    protected fun assertFileContent(name: String, expectedContent: List<String>) {
        Stopwatch.start()

        var i = 0
        File(name).forEachLine { line ->
            val expectedLine = expectedContent.getOrElse(i++) { "\n" }
            assertEquals(expectedLine, line)
        }

        Stopwatch.stop("assertFileContent")
    }
}
