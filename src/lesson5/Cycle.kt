package lesson5

import lesson5.Graph.*

class Cycle(v: Vertex) : Iterable<Vertex> {

    private class Node(val v: Vertex, var n: Node? = null)

    var size = 1
    private var root = Node(v)
    private var end = root

    fun attach(l: Cycle) {
        val v = l.root.v
        var current = root
        while (current.v != v) {
            current = current.n!!
        }
        val next = current.n
        current.n = l.root.n
        l.end.n = next
        size += l.size
    }

    fun add(v: Vertex) {
        end.n = Node(v)
        end = end.n!!
        size++
    }

    fun toList(): List<Vertex> {
        val list = mutableListOf<Vertex>()
        for (v in this) {
            list.add(v)
        }
        return list.toList()
    }

    override fun iterator(): Iterator<Vertex> {
        return LoopIterator()
    }

    private inner class LoopIterator : Iterator<Vertex> {
        private var current: Node? = root

        override fun next(): Vertex {
            val t = current
            current = current!!.n
            return t!!.v
        }

        override fun hasNext(): Boolean {
            return current != null
        }
    }
}