package lesson7.ants

import kotlin.math.pow
import kotlin.random.Random

/*
  реализация алгоритма базируется на двух статьях:
  https://www.researchgate.net/publication/279535061_Stovba_SD_Muravinye_algoritmy_Exponenta_Pro_Matematika_v_prilozenia
  h_-_2003_-_No4_-_S_70-75
  https://cyberleninka.ru/article/n/mnogomernaya-zadacha-o-ryukzake-novye-metody-resheniya
 */

class AntSolver(params: Params) {
    val random = Random(System.currentTimeMillis())

    inner class Ant {
        private var route = Route()

        fun buildRoute(pheromoneSignificanceSum: Double) {
            var validPaths: Set<Choosable> = paths
            while (validPaths.isNotEmpty()) {
                countPathsProbabilities(validPaths, pheromoneSignificanceSum)
                val path = choosePath()
                route.add(path)
                validPaths = paths.validPaths(route, validPaths)
            }
        }

        private fun countPathsProbabilities(validPaths: Set<Choosable>, pheromoneSignificanceSum: Double) {
            for (path in paths) {
                if (validPaths.contains(path)) {
                    path.probability = path.pheromoneSignificance / pheromoneSignificanceSum
                } else {
                    path.probability = 0.0
                }
            }
        }

        private fun choosePath(): Choosable {
            val roulette = MutableList(pathList.size) { 0.0 }

            roulette[0] = pathList[0].probability
            for (i in 1 until pathList.size) {
                roulette[i] = roulette[i - 1] + pathList[i].probability
            }

            val randDouble = random.nextDouble(roulette[pathList.lastIndex])
            val indexOfPath = findFirstBigger(roulette, randDouble)
            return pathList[indexOfPath]
        }

        private fun <E : Comparable<E>> findFirstBigger(map: MutableList<E>, n: E): Int {
            if (map.isEmpty()) return -1
            var l = 0
            var r = map.size - 1
            while (l <= r) {
                val m = (l + r).ushr(1)
                val compare1 = map[m].compareTo(n)
                if (compare1 > 0) {
                    if (m == l) return m
                    r = m
                } else {
                    l = m + 1
                }
            }
            return -1
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

    private lateinit var pathList: List<Choosable>
    private lateinit var paths: ChoosableSet

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