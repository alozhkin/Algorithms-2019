@file:Suppress("UNUSED_PARAMETER")

package lesson6

import lesson6.table.*
import java.io.File
import kotlin.math.ceil
import kotlin.math.max

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

    fun print(cell: Cell) {
        val direction = table.getDirection(cell)
        when {
            cell.line == -1 || cell.symbol == -1 -> return
            direction == Direction.UPPER_LEFT -> {
                res.append(short[cell.line])
            }
        }
        print(direction.getNext(cell))
    }

    print(Cell(short.length - 1, long.length - 1))

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
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    if (list.isEmpty()) return emptyList()
    val d = MutableList(list.size) { 0 }
    val p = MutableList(list.size) { 0 }
    for (i in list.indices) {
        var max = 0
        for (j in 0 until i) {
            if (list[j] < list[i] && d[j] > max) {
                max = d[j]
                p[i] = j
            }
        }
        d[i] = max(1, max + 1)
    }
    var maxI = 0
    var max = 0
    for (i in list.indices) {
        if (d[i] > max) {
            maxI = i
            max = d[i]
        }
    }
    val pos = mutableListOf<Int>()
    pos += list[maxI]
    while (d[maxI] != 1) {
        maxI = p[maxI]
        pos += list[maxI]
    }
    return pos.reversed()
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
    val source = parseFile(inputName)
    for ((cell, value) in source) {
        val upperLeft = source.getNeighbourValue(cell, Direction.UPPER_LEFT)
        val up = source.getNeighbourValue(cell, Direction.UP)
        val left = source.getNeighbourValue(cell, Direction.LEFT)
        if (!cell.isStarting()) {
            source[cell] = minOf(upperLeft, up, left) + value
        }
    }
    return source[Cell(source.height - 1, source.length - 1)]
}

fun parseFile(inputName: String): DirectionsTable<Int> {
    File(inputName).bufferedReader().use { reader ->
        val lines = reader.readLines()
        val length = ceil(lines[0].length.toDouble() / 2).toInt()
        val height = lines.size
        File(inputName).bufferedReader().use { reader2 ->
            val table = DirectionsTable(TableImpl(length, height, Int.MAX_VALUE))
            for ((cell, _) in table) {
                table[cell] = reader2.read() - '0'.toInt()
                reader2.read()
            }
            return table
        }
    }
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5