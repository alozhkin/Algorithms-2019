package lesson7.annotations

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.TestTemplateInvocationContext

class RerunContext(
    private val rerunNum: Int,
    private val runsTotal: Int,
    private val successPercentage: Int,
    private val failureCount: MutableInt
) : TestTemplateInvocationContext {
    override fun getDisplayName(invocationIndex: Int): String {
        return "run $invocationIndex of $runsTotal"
    }

    override fun getAdditionalExtensions(): MutableList<Extension> {
        return mutableListOf(
            RerunTestExtension(rerunNum, runsTotal, successPercentage, failureCount)
        )
    }
}