package lesson7

import lesson7.annotations.ReRun
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestTemplate


class HeuristicsTestsKotlin : AbstractHeuristicsTests() {

    @Test
    @Tag("Impossible")
    fun testFillKnapsack() {
        fillKnapsack()
    }

    @Tag("Impossible")
    @ReRun(10, 90)
    @TestTemplate
    fun testFillKnapsackWithALotOfItems() {
        fillKnapsackWithALotOfItems { load, items, params -> fillKnapsackHeuristics(load, items, params) }
    }

    @Tag("Impossible")
    @ReRun(10, 90)
    @TestTemplate
    fun testFillKnapsackWithTooThreeItems() {
        fillKnapsackWithTwoThreeItems { load, items, params -> fillKnapsackHeuristics(load, items, params) }
    }

    @Tag("Impossible")
    @ReRun(10, 90)
    @TestTemplate
    fun testFillKnapsackWithOneBestItem() {
        fillKnapsackWithOneBestItem { load, items, params -> fillKnapsackHeuristics(load, items, params) }
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