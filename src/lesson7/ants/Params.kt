package lesson7.ants

data class Params(
    val antNum: Int,
    val iterationNum: Int,
    val pheromoneExp: Double = 1.0,
    val significanceExp: Double = 1.0,
    val pheromone0: Double = 1.0,
    val pheromoneConst: Double = 1.0,
    val pheromoneEvaporationCoeff: Double = 0.3
)