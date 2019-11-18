package lesson7

import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import org.junit.jupiter.api.Tag
@Disabled
class HeuristicsTestsJava : AbstractHeuristicsTests() {

    @Test
    @Tag("Impossible")
    fun testFindVoyagingPathHeuristics() {
        findVoyagingPathHeuristics { let { JavaHeuristicsTasks.findVoyagingPathHeuristics(it) } }
    }

}