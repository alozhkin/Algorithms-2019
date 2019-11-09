@file:Suppress("UNUSED_PARAMETER")

package lesson6

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
enum class Direction(x: Int, y: Int) {
    UP(0, -1),
    LEFT(-1, 0),
    UPPER_LEFT(-1, -1)
}


fun longestCommonSubSequence(first: String, second: String): String {
    val first0 = ("0$first").toCharArray()
    val second0 = ("0$second").toCharArray()
    val c = MutableList(first0.size) { MutableList(second0.size) { 0 } }
    val b = MutableList(first0.size) { MutableList(second0.size) { Direction.UP } }
    for (i in 1..first0.size) {
        for (j in 1..second0.size) {
            when {
                first0[i] == second0[i] -> {
                    c[i][j] = c[i - 1][j - 1] + 1
                    b[i][j] = Direction.UPPER_LEFT
                }
                c[i - 1][j] >= c[i][j - 1] -> {
                    c[i][j] = c[i - 1][j]
                    b[i][j] = Direction.UP
                }
                else -> {
                    c[i][j] = c[i][j - 1]
                    b[i][j] = Direction.LEFT
                }
            }
        }
    }

    val res = StringBuilder()

    fun print(i: Int, j: Int) {
        when {
            i == 0 || j == 0 -> return
            b[i][j] == Direction.UPPER_LEFT -> {
                print(i - 1, j - 1)
                res.append(first0[i])
            }
            b[i][j] == Direction.UP -> print(i - 1, j)
            else -> print(i, j - 1)
        }
    }
    return res.toString()
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
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5