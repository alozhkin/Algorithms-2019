@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson7

import lesson5.Graph
import lesson5.Graph.*
import lesson5.Path
import lesson5.impl.GraphBuilder
import lesson6.knapsack.Fill
import lesson6.knapsack.Item
import lesson7.ants.*

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

fun fillKnapsackHeuristics(capacity: Int, items: List<Item>, vararg parameters: Any): Fill {
    val solver = AntSolver(Params(2000, 50))
    val choosableSet = KnapsackChoosableSet(items.toMutableSet(), capacity)
    val res = solver.solve(choosableSet)
    return Fill(res.cost.toInt(), res.toChoosableList().toSet())
}

class KnapsackChoosableSet(set: MutableSet<Choosable> = mutableSetOf(), private val capacity: Int) :
    ChoosableSet(set) {

    override fun tabooPaths(route: Route): Set<Choosable> {
        val res = mutableSetOf<Choosable>()
        res += route.toChoosableList()
        val load = route.limitation
        res += set.filter { path -> path.limitation + load > capacity }
        return res
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
    val solver = AntSolver(Params(2000, 50))
    val choosableSet = VoyagerChoosableSet(this.edges.map { it as Choosable }.toMutableSet(), 6, this)
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

class VoyagerChoosableSet(
    set: MutableSet<Choosable> = mutableSetOf(),
    private val verticesNum: Int,
    val graph: Graph
) :
    ChoosableSet(set) {

    private var prevLastVertex: Vertex? = null
    private var firstVertex: Vertex? = null

    override fun tabooPaths(route: Route): Set<Choosable> {
        if (route.size == verticesNum) return this

        val lastEdge = (route.last() as Edge)
        val lastVertex = if (route.size == 1) lastEdge.end else lastEdge.getOtherEnd(prevLastVertex)
        if (route.size == 1) {
            firstVertex = lastEdge.getOtherEnd(lastVertex)
        }
        prevLastVertex = lastVertex
        val t = route.toChoosableList().map { it as GraphBuilder.EdgeImpl }
        val visitedVertex = t.map { it.begin }.filter { it != lastVertex }.toMutableSet()
        visitedVertex += t.map { it.end }.filter { it != lastVertex }.toSet()

        if (visitedVertex.size == verticesNum - 1) {
            // todo явно есть нормальная функция
            return set.map { it as GraphBuilder.EdgeImpl }.filterNot {
                (it.begin == firstVertex && it.end == lastVertex)
                        || (it.begin == lastVertex && it.end == firstVertex)
            }.toSet()
        }

        val res = mutableSetOf<Choosable>()
        val connections = graph.getConnections(lastVertex).values
        res.addAll(set.map { it as GraphBuilder.EdgeImpl }
            .filter { it !in connections || it.begin in visitedVertex || it.end in visitedVertex })
        res.addAll(route.toChoosableList().toSet())
        return res
    }
}