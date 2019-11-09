@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson5

import lesson5.Graph.*
import lesson5.impl.GraphBuilder

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
fun Graph.findEulerLoop(): List<Edge> {
    if (vertices.isEmpty()
        || vertices.any { getVertexDegree(it) % 2 != 0 }
        || edges.size < vertices.size - 1
    ) {
        return emptyList()
    }

    var randomVertex = vertices.random()
    val vertexSet = mutableSetOf<Vertex>()

    fun findAllVertexInComponent(vertex: Vertex) {
        for (neighbour in getNeighbors(vertex)) {
            if (neighbour !in vertexSet) {
                vertexSet += neighbour
                findAllVertexInComponent(neighbour)
            }
        }
    }

    if (vertexSet.size != vertices.size) {
        return emptyList()
    }

    fun getAllEdgesInRightOrder(): List<Edge> {
        val edgesSet = mutableSetOf<Edge>()
        val edgesList = mutableListOf<Edge>()
        while (edgesSet.size != edges.size) {
            val connections = getConnections(randomVertex)
            val edge = connections.values.first { it !in edgesSet }
            edgesSet += edge
            edgesList += edge
            randomVertex = edge.getOtherEnd(randomVertex)
        }
        return edgesList
    }

    return getAllEdgesInRightOrder()
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
fun Graph.largestIndependentVertexSet(): Set<Vertex> {
    val firstVertices = getFirstVerticesOfConnectedComponents(this)
    val result = mutableSetOf<Vertex>()
    for (first in firstVertices) {
        result += getLargestIndependentVertexSet(this, first)
    }
    return result
}

private fun getFirstVerticesOfConnectedComponents(graph: Graph): Set<Vertex> {
    if (graph.vertices.isEmpty()) {
        return emptySet()
    }

    fun createConnectedComponentIfCyclesAreAbsent(
        v: Vertex,
        verticesSet: MutableSet<Vertex>,
        edgesSet: MutableSet<Edge>
    ): Set<Vertex> {
        for ((vertex, edge) in graph.getConnections(v)) {
            if (edge !in edgesSet) {
                if (vertex in verticesSet) {
                    throw IllegalArgumentException()
                } else {
                    verticesSet += vertex
                    edgesSet += edge
                    createConnectedComponentIfCyclesAreAbsent(vertex, verticesSet, edgesSet)
                }
            }
        }
        return verticesSet
    }

    fun findFirstVerticesOfConnectedComponents(): Set<Vertex> {
        val firstVertices = mutableSetOf<Vertex>()
        val connectedComponents = mutableSetOf<Set<Vertex>>()
        while (connectedComponents.flatten().size != graph.vertices.size) {
            val firstVertex = graph.vertices.first { it !in connectedComponents.flatten() }
            val verticesSet = createConnectedComponentIfCyclesAreAbsent(
                firstVertex, mutableSetOf(), mutableSetOf()
            )
            connectedComponents += verticesSet + firstVertex
            firstVertices += firstVertex
        }
        return firstVertices
    }

    return findFirstVerticesOfConnectedComponents()
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
fun Graph.longestSimplePath(): Path {
    TODO()
}