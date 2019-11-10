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
            var tabooPaths = emptySet<Choosable>()
            while (tabooPaths.size != paths.size) {
                countPathsProbabilities(tabooPaths, pheromoneSignificanceSum)
                val path = choosePath()
                route.add(path)
                tabooPaths = paths.tabooPaths(route)
            }
        }

        private fun countPathsProbabilities(tabooPaths: Set<Choosable>, pheromoneSignificanceSum: Double) {
            for (path in paths) {
                if (tabooPaths.contains(path)) {
                    path.probability = 0.0
                } else {
                    path.probability = path.pheromoneSignificance / pheromoneSignificanceSum
                }
            }
        }

        private fun choosePath(): Choosable {
            val roulette = mutableMapOf<Choosable, Double>()
            roulette[pathList[0]] = pathList[0].probability
            for (i in 1 until pathList.size) {
                roulette[pathList[i]] = roulette[pathList[i - 1]]!! + pathList[i].probability
            }
            val randDouble = random.nextDouble(roulette[pathList.last()]!!)
            var indexOfPath = 0
            while (roulette[pathList[indexOfPath]]!! < randDouble) {
                indexOfPath++
            }
            return pathList[indexOfPath]
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