package lesson5.impl

import lesson5.Graph.Edge
import lesson5.Graph
import lesson5.Graph.Vertex
import lesson7.ants.ChoosableImpl

class GraphBuilder {

    data class VertexImpl(private val nameField: String) : Vertex {
        override fun getName() = nameField

        override fun toString() = name
    }

    data class EdgeImpl(
        private val weightField: Int,
        private val _begin: Vertex,
        private val _end: Vertex
    ) : Edge, ChoosableImpl(1.0, weightField.toDouble()) {
        override fun getBegin() = _begin

        override fun getEnd() = _end

        override fun getWeight() = weightField

        override fun getOtherEnd(v: Vertex?): Vertex {
            return when (v) {
                begin -> end
                end -> begin
                else -> throw IllegalArgumentException()
            }
        }
    }

    private val vertices = mutableMapOf<String, Vertex>()

    private val connections = mutableMapOf<Vertex, Set<Edge>>()

    fun addVertex(v: Vertex) {
        vertices[v.name] = v
    }

    fun addVertex(name: String): Vertex {
        return VertexImpl(name).apply {
            addVertex(this)
        }
    }

    fun addConnection(begin: Vertex, end: Vertex, weight: Int = 1) {
        val edge = EdgeImpl(weight, begin, end)
        connections[begin] = connections[begin]?.let { it + edge } ?: setOf(edge)
        connections[end] = connections[end]?.let { it + edge } ?: setOf(edge)
    }

    fun addConnection(edge: Edge) {
        addConnection(edge.begin, edge.end, edge.weight)
    }

    fun build(): Graph = object : Graph {

        override fun get(name: String): Vertex? = this@GraphBuilder.vertices[name]

        override fun getVertices(): Set<Vertex> = this@GraphBuilder.vertices.values.toSet()

        override fun getEdges(): Set<Edge> {
            return connections.values.flatten().toSet()
        }

        override fun getConnections(v: Vertex): Map<Vertex, Edge> {
            val edges = connections[v] ?: emptySet()
            val result = mutableMapOf<Vertex, Edge>()
            for (edge in edges) {
                result[edge.getOtherEnd(v)] = edge
            }
            return result
        }

        override fun getVertexDegree(v: Vertex): Int {
            return connections[v]?.size ?: 0
        }

        override fun splitOnConnectedComponents(): Set<Graph> {
            val connectedComponents = mutableSetOf<Graph>()
            var allVertices = listOf<Vertex>()
            while (allVertices.size != this.vertices.size) {
                val firstVertex = this.vertices.first { it !in allVertices }
                val connectedComponent = connectedComponentOf(firstVertex)
                connectedComponents += connectedComponent
                allVertices = connectedComponents.flatMap { it.vertices }
            }
            return connectedComponents
        }

        override fun connectedComponentOf(firstVertex: Vertex): Graph {
            val verticesSet = mutableSetOf<Vertex>()
            val edgesSet = mutableSetOf<Edge>()

            fun findConnectedComponent(v: Vertex) {
                verticesSet.add(v)
                for ((vertex, edge) in this.getConnections(v)) {
                    if (edge !in edgesSet) {
                        verticesSet += vertex
                        edgesSet += edge
                        findConnectedComponent(vertex)
                    }
                }
            }

            findConnectedComponent(firstVertex)
            return GraphBuilder().apply {
                for (v in verticesSet) {
                    addVertex(v)
                }
                for (e in edgesSet) {
                    addConnection(e)
                }
            }.build()
        }

        override fun checkIfAcyclic(): Boolean {
            val visitedVertices = mutableSetOf<Vertex>()
            val visitedEdges = mutableSetOf<Edge>()

            fun dfs(v: Vertex): Boolean {
                if (v in visitedVertices) return false
                visitedVertices += v
                for ((neighbourVertex, neighbourEdge) in getConnections(v)) {
                    if (neighbourEdge !in visitedEdges) {
                        visitedEdges += neighbourEdge
                        if (!dfs(neighbourVertex)) return false
                    }
                }
                return true
            }

            return dfs(vertices.first())
        }
    }
}