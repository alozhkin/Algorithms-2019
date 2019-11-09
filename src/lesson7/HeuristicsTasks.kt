@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson7

import lesson5.Graph
import lesson5.Path
import lesson6.knapsack.Fill
import lesson6.knapsack.Item
import kotlin.math.pow
import kotlin.random.Random

// Примечание: в этом уроке достаточно решить одну задачу

/**
 * Решить задачу о ранце (см. урок 6) любым эвристическим методом
 *
 * Очень сложная
 *
 * capacity - общая вместимость ранца, items - список предметов
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
fun fillKnapsackHeuristics(capacity: Int, items: List<Item>, vararg parameters: Any): Fill {
    TODO()
    // todo можно сразу отрезать слишком большие предметы

    val antNum = 100
    val a = 2
    val b = 1
    val iterationNum = 200 //N
    val t0 = 0.01
    val pheromoneConst = 1 //q
    val pheromoneEvaporation = 0.3 //p [0..1]
    val pheromone = MutableList(items.size) { 1.0 }
    val tabuList = mutableListOf<Item>()
    val random = Random(System.currentTimeMillis())
    val significance = MutableList(items.size) { 0.0 }
    for (i in items.indices) {
        val item = items[i]
        significance[i] = (item.cost.toDouble() / item.weight).pow(b)
    }
    val choosen = MutableList(antNum) { 0 }

    fun takeItems() {
        val pheromoneSignificance = MutableList(items.size) { 0.0 }
        for (i in items.indices) {
            pheromoneSignificance[i] = pheromone[i].pow(a) * significance[i]
        }
        val pheromoneSignificanceSum = pheromoneSignificance.sum()
        for (antIndex in 0 until antNum) {
            val probability = MutableList(items.size) { 0.0 }
            val canTakeList = mutableListOf<Item>()
            val load = 0
            canTakeList += items.filter { it.weight <= capacity - load }
            for (i in items.indices) {
                if (!canTakeList.contains(items[i])) {
                    probability[i] = 0.0
                } else {
                    probability[i] = pheromoneSignificance[i] / pheromoneSignificanceSum
                }
            }
            val roulette = MutableList(items.size) { 0.0 }
            roulette[0] = probability[0]
            for (i in 1 until probability.size) {
                roulette[i] = roulette[i - 1] + probability[i]
            }
            // todo пространство для трудновоспроизводимой ошибки огромно
            val randDouble = random.nextDouble(roulette.last())
            var indexOfItem = 0
            while (roulette[indexOfItem] < randDouble) {
                indexOfItem++
            }
            choosen[antIndex] = indexOfItem
        }
    }

    for (i in 1..iterationNum) {
        takeItems()

    }

}



/**
 * Решить задачу коммивояжёра (см. урок 5) методом колонии муравьёв
 * или любым другим эвристическим методом, кроме генетического и имитации отжига
 * (этими двумя методами задача уже решена в под-пакетах annealing & genetic).
 *
 * Очень сложная
 *
 * Граф передаётся через получатель метода
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
fun Graph.findVoyagingPathHeuristics(vararg parameters: Any): Path {
    TODO()
}

