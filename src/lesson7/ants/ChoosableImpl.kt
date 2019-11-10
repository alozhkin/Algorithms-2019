package lesson7.ants

open class ChoosableImpl(override val value: Double, override val limitation: Double) : Choosable {
    override var pheromone = 0.0
    override var significance = 0.0
    override var pheromoneSignificance = 0.0
    override var probability = 0.0
}