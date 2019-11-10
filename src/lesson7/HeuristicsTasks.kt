@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson7

import lesson5.Graph
import lesson5.Graph.*
import lesson5.Path
import lesson5.impl.GraphBuilder
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
 * capacity - общая вместимость ранца, paths - список предметов
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */

// todo fill ограничивает меня, не давая повторять предметы
fun main() {

}

val random = Random(System.currentTimeMillis())

data class Params(
    val antNum: Int,
    val iterationNum: Int,
    val pheromoneExp: Double = 1.0,
    val significanceExp: Double = 1.0,
    val pheromone0: Double = 1.0,
    val pheromoneConst: Double = 1.0,
    val pheromoneEvaporationCoeff: Double = 0.3
)

class AntSolver(params: Params) {


    inner class Ant {
        private var route = Route()

        fun buildRoute(pheromoneSignificanceSum: Double) {
            var tabooPaths = emptySet<Choosable>()
            while (tabooPaths.size != paths.size) {
                countProbabilities(tabooPaths, pheromoneSignificanceSum)
                val path = choosePath()
                route.add(path)
                tabooPaths = paths.tabooPaths(route)
            }
        }

        private fun countProbabilities(tabooPaths: Set<Choosable>, pheromoneSignificanceSum: Double) {
            for (path in paths) {
                if (tabooPaths.contains(path)) {
                    // todo а чисто теоритически рандом может дать мне ноль и всё сломать?
                    path.probability = 0.0
                } else {
                    path.probability = path.pheromoneSignificance / pheromoneSignificanceSum
                }
            }
        }

        private fun choosePath(): Choosable {
            val roulette = mutableMapOf<Choosable, Double>()
            roulette[pathList[0]] = pathList[0].probability
            for (i in 1 until pathList.size) {
                roulette[pathList[i]] = roulette[pathList[i - 1]]!! + pathList[i].probability
            }
            // todo пространство для трудновоспроизводимой ошибки огромно
            val randDouble = random.nextDouble(roulette[pathList.last()]!!)
            var indexOfPath = 0
            while (roulette[pathList[indexOfPath]]!! < randDouble) {
                indexOfPath++
            }
            return pathList[indexOfPath]
        }

        fun getRouteValue(): Double {
            return route.value
        }

        fun getRouteLimitation(): Double {
            return route.limitation
        }

        fun getRoute(): Route {
            return route
        }

        fun goHome() {
            route = Route()
        }
    }


    val ants = List(params.antNum) { Ant() }
    val iterationNum = params.iterationNum
    val pheromoneExp = params.pheromoneExp
    val significanceExp = params.significanceExp
    val pheromone0 = params.pheromone0
    val pheromoneConst = params.pheromoneConst
    val pheromoneEvaporationCoeff = params.pheromoneEvaporationCoeff

    var pathList = emptyList<Choosable>()
    var paths = ChoosableSet(mutableSetOf())

    fun solve(choosableSet: ChoosableSet): Route {
        initializePaths(choosableSet)
        for (i in 1..iterationNum) {
            scatter()
            updatePheromones()
//            println(i)
        }
        val res = chooseBestRoute()
        return res
    }

    private fun initializePaths(choosableSet: ChoosableSet) {
        paths = choosableSet
        pathList = paths.set.toList()
        for (path in paths) {
            path.pheromone = pheromone0
            path.significance = (path.value / path.limitation).pow(significanceExp)
        }
    }

    private fun scatter() {
        for (ant in ants) {
            ant.goHome()
        }
        val pheromoneSignificanceSum = countPheromoneSignificanceSum()
        for (ant in ants) {
            ant.buildRoute(pheromoneSignificanceSum)
        }
    }

    private fun countPheromoneSignificanceSum(): Double {
        for (path in paths) {
            path.pheromoneSignificance = path.pheromone.pow(pheromoneExp) * path.significance
        }
        return paths.sumByDouble { it.pheromoneSignificance }
    }

    private fun updatePheromones() {
        val pheromoneDelta = mutableMapOf<Choosable, Double>()
        for (ant in ants) {
            for (path in ant.getRoute()) {
                //todo
                pheromoneDelta[path] = (pheromoneDelta[path] ?: 0.0) + pheromoneConst / ant.getRouteLimitation()
            }
        }
        for (path in paths) {
            path.pheromone = path.pheromone * (1 - pheromoneEvaporationCoeff) + (pheromoneDelta[path] ?: 0.0)
        }
    }

    private fun chooseBestRoute(): Route {
        var maxCost = 0.0
        var bestAnt = ants.first()
        for (ant in ants) {
            if (ant.getRouteValue() > maxCost) {
                bestAnt = ant
                maxCost = ant.getRouteValue()
            }
        }
        return bestAnt.getRoute()
    }
}


class Route(val list: MutableList<Choosable> = mutableListOf()) : MutableList<Choosable> by list {

    // todo не должно быть сеттера
    var limitation = 0.0
    var value = 0.0

    override fun add(element: Choosable): Boolean {
        limitation += element.limitation
        value += element.value
        return list.add(element)
    }

    fun getChoosable(): List<Choosable> {
        return list
    }
}


open class Choosable(val value: Double, val limitation: Double) {
    var pheromone = 0.0
    var significance = 0.0
    var pheromoneSignificance = 0.0
    var probability = 0.0
}


// todo переопределять для разных задач
open class ChoosableSet(val set: MutableSet<Choosable> = mutableSetOf()) :
    Set<Choosable> by set {

    var capacity: Int = Int.MAX_VALUE

    // todo ужасно
//    private fun clear() {
//        list.removeIf { it.limitation > capacity }
//    }

    open fun tabooPaths(route: Route): Set<Choosable> {
        val res = mutableSetOf<Choosable>()
        res += route.getChoosable()
        val load = route.limitation
        res += set.filter { it.limitation + load > capacity }
        return res
    }
}


class VoyagerChoosableSet(set: MutableSet<Choosable> = mutableSetOf(), val verticesNum: Int) :
    ChoosableSet(set) {

    var prevLastVertex: Vertex? = null
    var firstVertex: Vertex? = null

    override fun tabooPaths(route: Route): Set<Choosable> {
        val lastEdge = (route.last() as Edge)
        val lastVertex = if (route.size == 1) lastEdge.end else lastEdge.getOtherEnd(prevLastVertex)
        if (route.size == 1) {
            firstVertex = lastEdge.getOtherEnd(lastVertex)
        }
        prevLastVertex = lastVertex
        val t = route.getChoosable().map { it as GraphBuilder.EdgeImpl }
        val tabooVertices = t.map { it.begin }.filter { it != lastVertex }.toMutableSet()
        tabooVertices += t.map { it.end }.filter { it != lastVertex }.toSet()

        if (tabooVertices.size == verticesNum - 1) {
            return set.map { it as GraphBuilder.EdgeImpl }.filterNot {
                (it.begin == firstVertex && it.end == lastVertex)
                        || (it.begin == lastVertex && it.end == firstVertex)
            }.toSet()
        }

        val res =
            set.map { it as GraphBuilder.EdgeImpl }.filter {
                (it.begin in tabooVertices || it.end in tabooVertices)
                        || (it.begin != lastVertex && it.end != lastVertex)
            }.toSet()
        return res
    }
}


fun fillKnapsackHeuristics(capacity: Int, items: List<Item>, vararg parameters: Any): Fill {
    val solver = AntSolver(Params(2000, 50))
    val choosableSet = ChoosableSet(items.toMutableSet())
    choosableSet.capacity = capacity
    val res = solver.solve(choosableSet)
    return Fill(res.value.toInt(), res.getChoosable().toSet())
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
    val solver = AntSolver(Params(2000, 50))
    val choosableSet = VoyagerChoosableSet(this.edges.map { it as Choosable }.toMutableSet(), 6)
    val edgesList = solver.solve(choosableSet).map { it as GraphBuilder.EdgeImpl }
    val res = mutableListOf<Vertex>()
    res += edgesList[0].begin
    var last = edgesList[0].end
    for (i in 1 until edgesList.size) {
        res += last
        last = edgesList[i].getOtherEnd(last)
    }
    res += last
    return Path(res, edgesList.sumBy { it.weight })
}

