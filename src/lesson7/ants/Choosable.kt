package lesson7.ants

interface Choosable {
    val value: Double
    val limitation: Double
    var pheromone: Double
    var significance: Double
    var pheromoneSignificance: Double
    var probability: Double
}