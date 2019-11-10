package lesson7

import lesson5.Graph
import lesson5.Path
import lesson5.impl.GraphBuilder
import lesson6.knapsack.Fill
import lesson6.knapsack.Item
import lesson6.knapsack.fillKnapsackDynamic
import lesson6.knapsack.fillKnapsackGreedy
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractHeuristicsTests {

    fun fillKnapsackCompareWithGreedyTest(fillKnapsackHeuristics: (Int, List<Item>) -> Fill) {
        for (i in 0..9) {
            val items = mutableListOf<Item>()
            val random = Random()
            for (j in 0 until 1000) {
                items += Item(1 + random.nextInt(1000), 300 + random.nextInt(600))
            }
            try {
                val fillHeuristics = fillKnapsackHeuristics(1000, items)
                println("Heuristics score = " + fillHeuristics.cost)
                val fillGreedy = fillKnapsackGreedy(1000, items)
                println("Greedy score = " + fillGreedy.cost)
                assertTrue(fillHeuristics.cost >= fillGreedy.cost)
            } catch (e: StackOverflowError) {
                println("Greedy failed with Stack Overflow")
            }
        }
    }

    fun findVoyagingPathHeuristics(findVoyagingPathHeuristics: Graph.() -> Path) {
        val graph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            val f = addVertex("F")
            addConnection(a, b, 10)
            addConnection(a, c, 10000000)
            addConnection(a, d, 20)
            addConnection(a, e, 10000000)
            addConnection(a, f, 40)
            addConnection(b, c, 15)
            addConnection(b, d, 10)
            addConnection(b, e, 10000000)
            addConnection(b, f, 10000000)
            addConnection(c, d, 10000000)
            addConnection(c, e, 5)
            addConnection(c, f, 30)
            addConnection(d, e, 25)
            addConnection(d, f, 10000000)
            addConnection(e, f, 15)
        }.build()
        val path = graph.findVoyagingPathHeuristics()
        assertEquals(105, path.length)
        val vertices = path.vertices
        assertEquals(vertices.first(), vertices.last(), "Voyaging path $vertices must be loop!")
        val withoutLast = vertices.dropLast(1)
        val expected = listOf(graph["A"], graph["D"], graph["B"], graph["C"], graph["E"], graph["F"])
        assertEquals(expected.size, withoutLast.size, "Voyaging path $vertices must travel through all vertices!")
        expected.forEach {
            assertTrue(it in vertices, "Voyaging path $vertices must travel through all vertices!")
        }
    }

    fun fillKnapsack() {
        var capacity = 30
        var items = listOf(Item(8, 10), Item(5, 12), Item(6, 8), Item(10, 15), Item(4, 2))
        var myFill = fillKnapsackHeuristics(capacity, items)
        var optimalFill = fillKnapsackDynamic(capacity, items)
        var myLoad = myFill.items.fold(0.0) { prev, cur -> prev + cur.limitation }
        var optimalLoad = optimalFill.items.fold(0.0) { prev, cur -> prev + cur.limitation }
        require(myLoad <= capacity)
        println(myLoad)
        println(optimalLoad)
        println(myFill)
        println(optimalFill)
        println()

        capacity = 50
        items = listOf(
            Item(4, 4),
            Item(6, 6),
            Item(3, 7),
            Item(7, 3),
            Item(9, 1),
            Item(1, 9),
            Item(2, 2),
            Item(8, 8),
            Item(10, 10),
            Item(11, 11)
        )
        myFill = fillKnapsackHeuristics(capacity, items)
        optimalFill = fillKnapsackDynamic(capacity, items)
        myLoad = myFill.items.fold(0.0) { prev, cur -> prev + cur.limitation }
        optimalLoad = optimalFill.items.fold(0.0) { prev, cur -> prev + cur.limitation }
        require(myLoad <= capacity)
        println(myLoad)
        println(optimalLoad)
        println(myFill)
        println(optimalFill)
        println()

        capacity = 50
        items = listOf(
            Item(4, 4),
            Item(6, 6),
            Item(3, 7),
            Item(7, 3),
            Item(9, 1),
            Item(1, 9),
            Item(2, 2),
            Item(8, 8),
            Item(10, 10)
        )
        myFill = fillKnapsackHeuristics(capacity, items)
        optimalFill = fillKnapsackDynamic(capacity, items)
        myLoad = myFill.items.fold(0.0) { prev, cur -> prev + cur.limitation }
        optimalLoad = optimalFill.items.fold(0.0) { prev, cur -> prev + cur.limitation }
        require(myLoad <= capacity)
        println(myLoad)
        println(optimalLoad)
        println(myFill)
        println(optimalFill)
        println()
    }

}