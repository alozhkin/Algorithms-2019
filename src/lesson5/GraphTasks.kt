@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson5

import lesson5.Graph.*
import lesson5.impl.GraphBuilder
import java.util.*


/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */
/*
  Трудоёмкость: проходит один раз по каждому ребру: O(|E|)
  Ресурсоёмкость: хранит все вершины на пути: O(|E|)
 */
fun Graph.findEulerLoop(): List<Edge> {
    val visitedEdges = mutableSetOf<Edge>()

    fun createCycle(vertex: Vertex): Cycle? {
        val cycle = Cycle(vertex)

        fun findCycle(startingVertex: Vertex) {
            for ((neighbourVertex, neighbourEdge) in getConnections(startingVertex)) {
                if (neighbourEdge !in visitedEdges) {
                    if (neighbourVertex != vertex) {
                        cycle.add(neighbourVertex)
                        visitedEdges += neighbourEdge
                        findCycle(neighbourVertex)
                        break
                    } else {
                        visitedEdges += neighbourEdge
                        cycle.add(neighbourVertex)
                        return
                    }
                }
            }
        }

        findCycle(vertex)
        return if (cycle.size != 1) {
            val cycles = mutableListOf<Cycle>()
            for (cycleVertex in cycle) {
                val cycle2 = createCycle(cycleVertex) ?: continue
                cycles.add(cycle2)
            }
            for (loop in cycles) {
                cycle.attach(loop)
            }
            cycle
        } else {
            null
        }
    }

    if (edges.isEmpty()
        || vertices.any { getVertexDegree(it) % 2 != 0 }
        || edges.size < vertices.size - 1
    ) return emptyList()

    val randomVertex = edges.random().begin
    val cycle = createCycle(randomVertex)!!

    if (cycle.size < vertices.size) return emptyList()

    val list = cycle.toList()
    val res = mutableListOf<Edge>()
    for (i in 1 until list.size) {
        res.add(getConnection(list[i - 1], list[i])!!)
    }
    return res
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */
/*
  Трудоёмкость: обхожу все вершины O(|V|)
  Ресурсоёмкость: обхожу все вершины O(|V|)
 */
fun Graph.minimumSpanningTree(): Graph {
    if (vertices.isEmpty()) {
        return GraphBuilder().build()
    }
    val edgesList = mutableListOf<Edge>()
    val verticesSet = mutableSetOf<Vertex>()

    fun getSpanningTree(v: Vertex) {
        for ((vertex, edge) in getConnections(v)) {
            if (vertex !in verticesSet) {
                verticesSet += vertex
                edgesList += edge
                getSpanningTree(vertex)
            }
        }
    }

    val randomVertex = vertices.random()
    verticesSet += randomVertex
    getSpanningTree(randomVertex)

    return GraphBuilder().apply {
        addVertex(randomVertex)
        if (edgesList[0].begin == randomVertex) {
            for (edge in edgesList) {
                addVertex(edge.end)
                addConnection(edge.begin, edge.end)
            }
        } else {
            for (edge in edgesList) {
                addVertex(edge.begin)
                addConnection(edge.begin, edge.end)
            }
        }
    }.build()
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 */
/*
  Трудоёмкость: O(|V| + |E|)
  Ресурсоёмкость: O(|V|)
 */
fun Graph.largestIndependentVertexSet(): Set<Vertex> {
    val connectedComponents = splitOnConnectedComponents()
    require(connectedComponents.all { it.checkIfAcyclic() })
    val firstVertices = connectedComponents.map { it.vertices.first() }
    val result = mutableSetOf<Vertex>()
    for (first in firstVertices) {
        result += getLargestIndependentVertexSet(this, first)
    }
    return result
}

private fun getLargestIndependentVertexSet(graph: Graph, firstVertex: Vertex): Set<Vertex> {
    val I = mutableMapOf<Vertex, Int>()
    var childrenFlag = false
    val visited = mutableSetOf<Vertex>()

    fun findLargestIndependentVertexSet(v: Vertex): Int {
        if (I[v] != null) return I[v]!!
        visited += v
        var childrenSum = 0
        var grandChildrenSum = 0
        for (child in graph.getNeighbors(v)) {
            if (child !in visited) {
                childrenSum += findLargestIndependentVertexSet(child)
            }
        }
        val grandChildren = mutableSetOf<Vertex>()
        for (child in graph.getNeighbors(v)) {
            if (child !in visited) {
                for (grandChild in graph.getNeighbors(child)) {
                    grandChildren += grandChild
                }
            }
        }
        for (grandChild in grandChildren) {
            grandChildrenSum += findLargestIndependentVertexSet(grandChild)
        }
        if (grandChildrenSum + 1 >= childrenSum) {
            I[v] = grandChildrenSum
            childrenFlag = false
        } else {
            I[v] = childrenSum
            childrenFlag = true
        }
        return I[v]!!
    }

    findLargestIndependentVertexSet(firstVertex)
    val largestIndependentVertexSet = mutableSetOf<Vertex>()
    visited.clear()

    fun createLargestIndependentVertexSet(vertices: Set<Vertex>) {
        for (v in vertices) {
            val grandChildren = mutableSetOf<Vertex>()
            for (child in graph.getNeighbors(v)) {
                visited += child
                for (grandChild in graph.getNeighbors(child)) {
                    if (grandChild !in visited) {
                        grandChildren += grandChild
                        visited += grandChild
                    }
                }
            }
            largestIndependentVertexSet += grandChildren
            createLargestIndependentVertexSet(grandChildren)
        }
    }

    if (childrenFlag) {
        val children = graph.getNeighbors(firstVertex)
        largestIndependentVertexSet += children
        visited += children
        createLargestIndependentVertexSet(children)
    } else {
        largestIndependentVertexSet += firstVertex
        visited += firstVertex
        createLargestIndependentVertexSet(setOf(firstVertex))
    }
    return largestIndependentVertexSet
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */
/*
  пусть n - длина самого длинного простого пути
  Трудоёмкость: O(n!)
  Ресурсоёмкость: O(n!)
 */
fun Graph.longestSimplePath(): Path {
    val queue = PriorityQueue<Path>(Comparator.reverseOrder())
    var longestPath = Path()
    var length = -1
    for (vertex in vertices) {
        queue.add(Path(vertex))
    }
    while (!queue.isEmpty()) {
        val path = queue.poll()
        if (path.length > length) {
            longestPath = path
            length = path.length
            if (path.vertices.size == vertices.size) {
                break
            }
        }
        for (neighbour in getNeighbors(path.vertices.last())) {
            if (neighbour !in path) {
                queue.add(Path(path, this, neighbour))
            }
        }
    }
    return longestPath
}



