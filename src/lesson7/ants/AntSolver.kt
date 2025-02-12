package lesson7.ants

import kotlin.math.pow
import kotlin.random.Random
import java.util.concurrent.*


/*
  реализация алгоритма базируется на двух статьях:
  https://www.researchgate.net/publication/279535061_Stovba_SD_Muravinye_algoritmy_Exponenta_Pro_Matematika_v_prilozenia
  h_-_2003_-_No4_-_S_70-75
  https://cyberleninka.ru/article/n/mnogomernaya-zadacha-o-ryukzake-novye-metody-resheniya
 */

class AntSolver(params: Params) {

    inner class Ant {
        private var route = Route()

        fun buildRoute() {
            var validPaths = pathList
            while (validPaths.isNotEmpty()) {
                val path = choosePath(validPaths)
                route.add(path)
                validPaths = paths.validPaths(route, validPaths)
            }
        }

        private fun choosePath(validPaths: List<Choosable>): Choosable {
            val roulette = MutableList(validPaths.size) { 0.0 }

            roulette[0] = validPaths[0].probability
            for (i in 1 until validPaths.size) {
                roulette[i] = roulette[i - 1] + validPaths[i].probability
            }

            val randDouble = random.nextDouble(roulette[validPaths.lastIndex])
            val indexOfPath = findFirstBigger(roulette, randDouble)
            return validPaths[indexOfPath]
        }

        private fun <E : Comparable<E>> findFirstBigger(list: MutableList<E>, n: E): Int {
            if (list.isEmpty()) return -1
            var l = 0
            var r = list.size - 1
            while (l <= r) {
                val m = (l + r).ushr(1)
                val compare1 = list[m].compareTo(n)
                if (compare1 > 0) {
                    if (m == l) return m
                    r = m
                } else {
                    l = m + 1
                }
            }
            throw IllegalArgumentException("Haven't found something bigger than $n in $list")
        }

        fun getRouteCost(): Double {
            return route.cost
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


    private val ants = List(params.antNum) { Ant() }
    private val iterationNum = params.iterationNum
    private val pheromoneExp = params.pheromoneExp
    private val significanceExp = params.significanceExp
    private val pheromone0 = params.pheromone0
    private val pheromoneConst = params.pheromoneConst
    private val pheromoneEvaporationCoeff = params.pheromoneEvaporationCoeff

    private val random = Random(System.currentTimeMillis())

    private lateinit var pathList: List<Choosable>
    private lateinit var paths: ChoosableSet

    private val exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 2)

    fun solve(choosableSet: ChoosableSet): Route {
        initializePaths(choosableSet)
        for (i in 1..iterationNum) {
            scatter()
            updatePheromones()
        }
        return chooseBestRoute()
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

        countPathsProbabilities()

        val tasks = mutableListOf<Callable<Unit>>()
        for (ant in ants) {
            tasks += Callable { ant.buildRoute() }
        }
        exec.invokeAll(tasks)
    }

    private fun countPathsProbabilities() {
        for (path in paths) {
            path.pheromoneSignificance = path.pheromone.pow(pheromoneExp) * path.significance
        }
        val pheromoneSignificanceSum = paths.sumByDouble { it.pheromoneSignificance }
        for (path in paths) {
            path.probability = path.pheromoneSignificance / pheromoneSignificanceSum
        }
    }

    private fun updatePheromones() {
        val pheromoneDelta = mutableMapOf<Choosable, Double>()
        for (ant in ants) {
            for (path in ant.getRoute()) {
                pheromoneDelta[path] =
                    (pheromoneDelta[path] ?: 0.0) + pheromoneConst / ant.getRouteLimitation() * ant.getRouteCost()
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
            if (ant.getRouteCost() > maxCost) {
                bestAnt = ant
                maxCost = ant.getRouteCost()
            }
        }
        return bestAnt.getRoute()
    }
}