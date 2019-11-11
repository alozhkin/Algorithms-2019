package lesson7

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class HeuristicsTestsKotlin : AbstractHeuristicsTests() {
    @Test
    @Tag("Impossible")
    fun testFillKnapsack() {
        fillKnapsack()
    }

    // этот тест занимает примерно 2 минуты
    @Test
    @Tag("Impossible")
    fun testFillKnapsackCompareWithGreedyTest() {
        fillKnapsackCompareWithGreedyTest { load, items, params -> fillKnapsackHeuristics(load, items, params) }
    }

    @Test
    @Tag("Impossible")
    fun testFindVoyagingPathHeuristics() {
        findVoyagingPathHeuristics { findVoyagingPathHeuristics() }
    }

    @Test
    @Tag("Impossible")
    fun testFindRandomVoyagingPath() {
        findRandomVoyagingPath { params -> findVoyagingPathHeuristics(params) }
    }
}