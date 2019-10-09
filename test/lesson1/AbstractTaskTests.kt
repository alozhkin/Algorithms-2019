package lesson1

import org.junit.jupiter.api.Assertions.assertArrayEquals
import util.PerfResult
import util.estimate
import java.io.BufferedWriter
import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.system.measureNanoTime
import kotlin.test.assertTrue

abstract class AbstractTaskTests : AbstractFileTests() {
    private val MOST_FREQUENT_ELEM_INVERSE_RATIO = 20

    protected fun sortTimes(sortTimes: (String, String) -> Unit) {
        try {
            sortTimes("input/time_in1.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                     12:40:31 AM
                     07:26:57 AM
                     10:00:03 AM
                     01:15:19 PM
                     01:15:19 PM
                     07:56:14 PM
                """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortTimes("input/time_in2.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                     12:00:00 AM
                """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortTimes("input/time_in3.txt", "temp.txt")
            assertFileContent("temp.txt", File("input/time_out3.txt").readLines())
        } finally {
            File("temp.txt").delete()
        }
    }

    protected fun sortAddresses(sortAddresses: (String, String) -> Unit) {
        try {
            sortAddresses("input/addr_in1.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                    Железнодорожная 3 - Петров Иван
                    Железнодорожная 7 - Иванов Алексей, Иванов Михаил
                    Садовая 5 - Сидоров Петр, Сидорова Мария
                """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortAddresses("input/addr_in2.txt", "temp.txt")
            assertFileContent("temp.txt", File("input/addr_out2.txt").readLines())
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortAddresses("input/addr_in3.txt", "temp.txt")
            assertFileContent("temp.txt", File("input/addr_out3.txt").readLines())
        } finally {
            File("temp.txt").delete()
        }
    }

    private fun generateTemperatures(size: Int): PerfResult<Unit> {
        Stopwatch.start()

        val tempsCount = mutableMapOf<Int, Int>()
        val random = Random()
        for (t in -2730..5000) {
            val count = random.nextInt(size)
            tempsCount[t] = count
        }

        fun BufferedWriter.writeTemperatures(temps: List<Int>) {
            for (t in temps) {
                for (i in 0 until tempsCount[t]!!) {
                    if (t < 0) write("-")
                    write("${abs(t) / 10}.${abs(t) % 10}")
                    newLine()
                }
            }
            close()
        }

        File("temp_sorted_expected.txt").bufferedWriter()
            .writeTemperatures(tempsCount.keys.toSortedSet().toList())
        File("temp_unsorted.txt").bufferedWriter()
            .writeTemperatures(tempsCount.keys.toList().shuffled())

        Stopwatch.stop("generateTemperatures")

        return PerfResult(size = tempsCount.values.sum(), data = Unit)
    }

    protected fun sortTemperatures(sortTemperatures: (String, String) -> Unit) {
        try {
            sortTemperatures("input/my_test_temp.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                    -3.0
                    -3.0
                    3.0
                    3.0
                    3.0
                """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortTemperatures("input/temp_in1.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                    -98.4
                    -12.6
                    -12.6
                    11.0
                    24.7
                    99.5
                    121.3
                """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }

        fun testGeneratedTemperatures(size: Int): PerfResult<Unit> {
            try {
                val res = generateTemperatures(size)
                val time = measureNanoTime { sortTemperatures("temp_unsorted.txt", "temp_sorted_actual.txt") }
                //assertFileContent("temp_sorted_actual.txt", File("temp_sorted_expected.txt").readLines())
                //slow, but informative
                //JavaFileTests.assertFilesByLines("temp_sorted_actual.txt", "temp_sorted_expected.txt")

                //fast, but uninformative
                assertTrue(JavaFileTests.compareFilesByBytes("temp_sorted_actual.txt", "temp_sorted_expected.txt"))
                return res.copy(time = time)
            } finally {
                File("temp_unsorted.txt").delete()
                File("temp_sorted_expected.txt").delete()
                File("temp_sorted_actual.txt").delete()
            }
        }

        val perf = estimate(listOf(10, 100, 1000, 10000)) {
            testGeneratedTemperatures(it)
        }
        println("sortTemperatures: $perf")

//        testGeneratedTemperatures(100_000)
    }


    private fun generateSequence(totalSize: Int, answerSize: Int): PerfResult<Unit> {
        Stopwatch.start()

        val integerCache = mutableMapOf<Int, Int>()
        val random = Random()
        val numbers = mutableListOf<Int>()

        val answer = 100000 + random.nextInt(100000)
        val count = mutableMapOf<Int, Int>()
        for (i in 1..totalSize - answerSize) {
            var next: Int
            var nextCount: Int
            do {
                next = random.nextInt(answer - 1) + 1
                nextCount = count[next] ?: 0
            } while (nextCount >= answerSize - 1)
            integerCache.putIfAbsent(next, next)
            integerCache.putIfAbsent(nextCount + 1, nextCount + 1)
            val nextFromCache = integerCache[next]!!
            val nextCountPlusOneFromCache = integerCache[nextCount + 1]!!
            numbers += nextFromCache
            count[nextFromCache] = nextCountPlusOneFromCache
        }
        for (i in totalSize - answerSize + 1..totalSize) {
            numbers += answer
        }

        fun BufferedWriter.writeNumbers(numbers: List<Int>) {
            for (n in numbers) {
                write("$n")
                newLine()
            }
            close()
        }

        File("temp_sequence_expected.txt").bufferedWriter().writeNumbers(numbers)

        var elemCount = 0
        var notElemCount = totalSize - 1
        for (i in totalSize - 1..0) {
            val isElem = elemCount < answerSize && random.nextInt(MOST_FREQUENT_ELEM_INVERSE_RATIO) == 0
            var toInsert: Int
            if (!isElem && notElemCount > 0) {
                toInsert = numbers[notElemCount]
                --notElemCount
            } else {
                toInsert = answer
                ++elemCount
            }
            numbers[i] = toInsert
        }
        File("temp_sequence.txt").bufferedWriter().writeNumbers(numbers)

        Stopwatch.stop("generateSequence")

        return PerfResult(size = numbers.size, data = Unit)
    }

    protected fun sortSequence(sortSequence: (String, String) -> Unit) {
        try {
            sortSequence("input/my_test_seq.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                        3
                        11
                        11
                    """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortSequence("input/seq_in1.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                        1
                        3
                        3
                        1
                        2
                        2
                        2
                    """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortSequence("input/seq_in2.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                        25
                        39
                        25
                        39
                        25
                        39
                        12
                        12
                        12
                    """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortSequence("input/seq_in3.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                        25986000
                        39234000
                        25986000
                        39234000
                        25986000
                        39234000
                        12345000
                        12345000
                        12345000
                    """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortSequence("input/seq_in4.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                        78
                        32
                        13
                        49
                        32
                        85
                        91
                        41
                        25
                        25
                    """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortSequence("input/seq_in5.txt", "temp.txt")
            assertFileContent(
                "temp.txt",
                """
                        78
                        13
                        45
                        49
                        85
                        45
                        91
                        41
                        32
                        32
                    """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }

        fun testGeneratedSequence(totalSize: Int, answerSize: Int): PerfResult<Unit> {
            try {
                val res = generateSequence(totalSize, answerSize)
                val time = measureNanoTime { sortSequence("temp_sequence.txt", "temp.txt") }

                //slow, but informative
                //JavaFileTests.assertFilesByLines("temp.txt", "temp_sequence_expected.txt")

                //fast, but uninformative
                assertTrue(JavaFileTests.compareFilesByBytes("temp.txt", "temp_sequence_expected.txt"));

                return res.copy(time = time)
            } finally {
                File("temp_sequence_expected.txt").delete()
                File("temp_sequence.txt").delete()
                File("temp.txt").delete()
            }
        }

        val perf = estimate(listOf(1_000, 10_000, 100_000, 1_000_000, 10_000_000)) {
            testGeneratedSequence(it, it / MOST_FREQUENT_ELEM_INVERSE_RATIO)
        }
        println("sortSequence: $perf")

//        testGeneratedPrices(100_000_000, 100_000_000 / MOST_FREQUENT_ELEM_INVERSE_RATIO)
    }

    private fun generateArrays(
        firstSize: Int,
        secondSize: Int
    ): PerfResult<Triple<Array<Int>, Array<Int?>, Array<Int?>>> {
        val random = Random()
        val expectedResult = Array<Int?>(firstSize + secondSize) {
            it * 10 + random.nextInt(10)
        }
        val first = mutableListOf<Int>()
        val second = mutableListOf<Int?>()
        for (i in 1..firstSize) second.add(null)
        for (element in expectedResult) {
            if (first.size < firstSize && (random.nextBoolean() || second.size == firstSize + secondSize)) {
                first += element!!
            } else {
                second += element
            }
        }
        return PerfResult(
            size = expectedResult.size,
            data = Triple(first.toTypedArray(), second.toTypedArray(), expectedResult)
        )
    }

    protected fun mergeArrays(mergeArrays: (Array<Int>, Array<Int?>) -> Unit) {
        val result = arrayOf(null, null, null, null, null, 1, 3, 9, 13, 18, 23)
        mergeArrays(arrayOf(4, 9, 15, 20, 23), result)
        assertArrayEquals(arrayOf(1, 3, 4, 9, 9, 13, 15, 18, 20, 23, 23), result)

        fun testGeneratedArrays(
            firstSize: Int,
            secondSize: Int
        ): PerfResult<Triple<Array<Int>, Array<Int?>, Array<Int?>>> {
            val res = generateArrays(firstSize, secondSize)
            val (first, second, expectedResult) = res.data
            val time = measureNanoTime { mergeArrays(first, second) }
            assertArrayEquals(expectedResult, second)
            return res.copy(time = time)
        }

        val perf = estimate(listOf(1_000, 10_000, 100_000, 1_000_000)) {
            testGeneratedArrays(it, it)
        }

        println("mergeArrays: $perf")
    }
}
