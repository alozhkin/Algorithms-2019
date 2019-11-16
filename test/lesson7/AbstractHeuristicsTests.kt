package lesson7

import lesson5.Graph
import lesson5.Path
import lesson5.impl.GraphBuilder
import lesson6.knapsack.Fill
import lesson6.knapsack.Item
import lesson6.knapsack.fillKnapsackDynamic
import lesson6.knapsack.fillKnapsackGreedy
import lesson7.annealing.findVoyagingPathAnnealing
import lesson7.ants.Params
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractHeuristicsTests {

    fun fillKnapsackWithALotOfItems(fillKnapsackHeuristics: (Int, List<Item>, Params) -> Fill) {
        fillKnapsackCompareWithGreedyTest(
            fillKnapsackHeuristics,
            Params(600, 200, 1.0, 3.0),
            200,
            500,
            60,
            1000
        )
    }

    fun fillKnapsackWithTwoThreeItems(fillKnapsackHeuristics: (Int, List<Item>, Params) -> Fill) {
        fillKnapsackCompareWithGreedyTest(
            fillKnapsackHeuristics,
            Params(2000, 50, 1.0, 1.0),
            1000,
            1000,
            600,
            1000
        )
    }

    fun fillKnapsackWithOneBestItem(fillKnapsackHeuristics: (Int, List<Item>, Params) -> Fill) {
        fillKnapsackCompareWithGreedyTest(
            fillKnapsackHeuristics,
            Params(2000, 50, 1.0, 1.0),
            1000,
            100,
            600,
            1000
        )
    }

    val random = Random()

    private fun fillKnapsackCompareWithGreedyTest(
        fillKnapsackHeuristics: (Int, List<Item>, Params) -> Fill,
        params: Params,
        itemsNum: Int,
        costBound: Int,
        approximateWeight: Int,
        maxLoad: Int
    ) {
        val items = mutableListOf<Item>()
        for (j in 0 until itemsNum) {
            items += Item(
                1 + random.nextInt(costBound),
                approximateWeight / 2 + random.nextInt(approximateWeight)
            )
        }
        var nextFuncToBeDone = "Greedy"
        try {
            val fillHeuristics = fillKnapsackHeuristics.invoke(maxLoad, items, params)
            println("Heuristics score = " + fillHeuristics.cost)
            val fillGreedy = fillKnapsackGreedy(maxLoad, items)
            println("Greedy score = " + fillGreedy.cost)
            nextFuncToBeDone = "Dynamic"
            val fillDynamic = fillKnapsackDynamic(maxLoad, items)
            println("Dynamic score = " + fillDynamic.cost)
            assertTrue(fillHeuristics.cost >= fillGreedy.cost)
        } catch (e: StackOverflowError) {
            println("$nextFuncToBeDone failed with Stack Overflow")
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
            addConnection(a, d, 20)
            addConnection(a, f, 40)
            addConnection(b, c, 15)
            addConnection(b, d, 10)
            addConnection(c, e, 5)
            addConnection(c, f, 30)
            addConnection(d, e, 25)
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

    fun findRandomVoyagingPath(findVoyagingPathHeuristics: Graph.(Params) -> Path) {
        val random = Random()
        val graph = GraphBuilder().apply {
            val vertices = mutableListOf<Graph.Vertex>()
            for (i in 0..99) {
                vertices += addVertex(i.toString())
            }
            for (i in 0..99) {
                for (j in i + 1..99) {
                    addConnection(vertices[i], vertices[j], 1 + random.nextInt(100))
                }
            }
        }.build()
        val pathActual = graph.findVoyagingPathHeuristics(
            Params(
                200,
                50
            )
        )
        val pathExpected = graph.findVoyagingPathAnnealing(startTemperature = 3000, iterationNumber = 1000)
        println("Heuristic length ${pathActual.length}")
        println("Annealing length ${pathExpected.length}")
        assertTrue(pathActual < pathExpected)

        val vertices = pathActual.vertices
        assertEquals(vertices.first(), vertices.last(), "Voyaging path $vertices must be loop!")
        val withoutLast = vertices.dropLast(1)
        assertEquals(graph.vertices.size, withoutLast.size, "Voyaging path $vertices must travel through all vertices!")
        graph.vertices.forEach {
            assertTrue(it in vertices, "Voyaging path $vertices must travel through all vertices!")
        }

    }

    fun fillKnapsack() {
        var capacity = 30
        var items = listOf(
            Item(8, 10),
            Item(5, 12),
            Item(6, 8),
            Item(10, 15),
            Item(4, 2)
        )
        var myLoad = fillKnapsackHeuristics(capacity, items).items.fold(0.0) { prev, cur -> prev + cur.limitation }
        var optimalLoad = fillKnapsackDynamic(capacity, items).items.fold(0.0) { prev, cur -> prev + cur.limitation }
        require(myLoad <= capacity)
        println("heuristic: $myLoad")
        println("dynamic: $optimalLoad")
        println()
        assertEquals(optimalLoad, myLoad)

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

        myLoad = fillKnapsackHeuristics(capacity, items).items.fold(0.0) { prev, cur -> prev + cur.limitation }
        optimalLoad = fillKnapsackDynamic(capacity, items).items.fold(0.0) { prev, cur -> prev + cur.limitation }
        require(myLoad <= capacity)
        println("heuristic: $myLoad")
        println("dynamic: $optimalLoad")
        println()
        assertEquals(optimalLoad, myLoad)

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

        myLoad = fillKnapsackHeuristics(capacity, items).items.fold(0.0) { prev, cur -> prev + cur.limitation }
        optimalLoad = fillKnapsackDynamic(capacity, items).items.fold(0.0) { prev, cur -> prev + cur.limitation }
        require(myLoad <= capacity)
        println("heuristic: $myLoad")
        println("dynamic: $optimalLoad")
        println()
        assertEquals(optimalLoad, myLoad)
    }
}