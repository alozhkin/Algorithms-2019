@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson7

import lesson1.Stopwatch
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
    Stopwatch.start()

    val solver = AntSolver(Params(2000, 50))
    val choosableSet = KnapsackChoosableSet(items.toMutableSet(), capacity)
    val res = solver.solve(choosableSet)

    Stopwatch.stop("fillKnapsackHeuristics")

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

fun Graph.findVoyagingPathHeuristics(params: Params? = null): Path {
    val completeGraph = createCompleteGraph(this)
    val parameters = params ?: Params(2000, 50)
    val solver = AntSolver(parameters)
    val choosableSet = VoyagerChoosableSet(
        completeGraph.edges.toMutableSet(),
        completeGraph.vertices.size,
        completeGraph
    )
    val route = solver.solve(choosableSet)
    return createPath(route)
}

private fun createCompleteGraph(graph: Graph): Graph {
    val vertexList = graph.vertices.toList()
    return GraphBuilder().apply {
        for (i in vertexList.indices) {
            addVertex(vertexList[i])
            for (j in i + 1..vertexList.lastIndex) {
                val connection = graph.getConnection(vertexList[i], vertexList[j])
                val weight = connection?.weight ?: 100_000_000
                addConnection(vertexList[i], vertexList[j], weight)
            }
        }
    }.build()
}

private fun createPath(route: Route): Path {
    val edgesList = route.map { it as Edge }
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