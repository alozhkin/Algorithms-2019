@file:Suppress("UNUSED_PARAMETER")

package lesson6

import lesson6.table.*
import java.io.File
import kotlin.math.ceil

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */

fun longestCommonSubSequence(first: String, second: String): String {
    val short = if (first.length <= second.length) first else second
    val long = if (first.length > second.length) first else second

    val table = DirectionsTable(TableImpl(short.length, long.length, 0))
    for ((cell, _) in table) {
        when {
            short[cell.line] == long[cell.symbol] -> {
                val neighbour = table.getNeighbourValue(cell, Direction.UPPER_LEFT)
                table.set(cell, Direction.UPPER_LEFT, neighbour + 1)
            }
            table.getNeighbourValue(cell, Direction.UP) >= table.getNeighbourValue(cell, Direction.LEFT) -> {
                table.refer(cell, Direction.UP)
            }
            else -> {
                table.refer(cell, Direction.LEFT)
            }
        }
    }

    val res = StringBuilder()

    fun restore(cell: Cell) {
        val direction = table.getDirection(cell)
        when {
            cell.line == -1 || cell.symbol == -1 -> return
            direction == Direction.UPPER_LEFT -> {
                res.append(short[cell.line])
            }
        }
        restore(direction.getNext(cell))
    }

    restore(Cell(short.length - 1, long.length - 1))

    return res.reverse().toString()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
// todo можно приколоться и сделать таблицу с линкед листами для этого задания
// todo n^2 ой как не хорошо
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    if (list.isEmpty()) return emptyList()
    val longestSubSeq = MutableList(list.size) { 0 }
    val prevElem = MutableList(list.size) { 0 }

    fun count() {
        for (i in list.indices) {
            var max = 0
            for (j in 0 until i) {
                if (list[j] < list[i] && longestSubSeq[j] > max) {
                    max = longestSubSeq[j]
                    prevElem[i] = j
                }
            }
            longestSubSeq[i] = max + 1
        }
    }

    fun restore(): List<Int> {
        val max = longestSubSeq.max()
        var maxIndex = longestSubSeq.indexOf(max)
        val pos = mutableListOf<Int>()
        pos += list[maxIndex]
        while (longestSubSeq[maxIndex] != 1) {
            maxIndex = prevElem[maxIndex]
            pos += list[maxIndex]
        }
        return pos.reversed()
    }

    count()
    return restore()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    File(inputName).bufferedReader().use { reader ->
        val firstLine = reader.readLine()
        val length = ceil(firstLine.length.toDouble() / 2).toInt()
        val table = InfinityTable(length, Int.MAX_VALUE)
        table[Cell(0, 0)] = 0
        val iterator = table.iterator()

        fun parseLine(line: String) {
            for (i in line.indices step 2) {
                val (cell, _) = iterator.next()
                val num = line[i].toInt() - '0'.toInt()
                val upperLeft = table.getNeighbourValue(cell, Direction.UPPER_LEFT)
                val up = table.getNeighbourValue(cell, Direction.UP)
                val left = table.getNeighbourValue(cell, Direction.LEFT)
                table[cell] = minOf(upperLeft, up, left) + num
            }
        }

        parseLine(firstLine)
        for (line in reader.readLines()) {
            parseLine(line)
        }

        return table[Cell(table.height - 1, length - 1)]
    }
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5