package lesson7.annotations

import org.junit.jupiter.api.extension.*
import org.opentest4j.TestAbortedException
import kotlin.math.ceil

class RerunTestExtension(
    private val rerunNum: Int,
    private val runsTotal: Int,
    private val successPercentage: Int,
    private var failureCount: MutableInt
) : ExecutionCondition, TestExecutionExceptionHandler {

    private val minSuccessTries = ceil((successPercentage * runsTotal).toDouble() / 100).toInt()

    override fun evaluateExecutionCondition(
        context: ExtensionContext
    ): ConditionEvaluationResult {
        val successCount = -failureCount + (rerunNum - 1)
        return when {
            (-failureCount + runsTotal) < minSuccessTries ->
                ConditionEvaluationResult.disabled(
                    "Cannot hit success percentage of " +
                            "$successPercentage% - ${(failureCount.toDouble() / runsTotal * 100).toInt()}% " +
                            "of failures already. Skipping run $rerunNum"
                )
            successCount < minSuccessTries ->
                ConditionEvaluationResult.enabled(
                    "${runsTotal - rerunNum - 1} runs left"
                )
            else -> ConditionEvaluationResult.disabled(
                "$successPercentage% have already done. Skipping run $rerunNum"
            )
        }
    }

    override fun handleTestExecutionException(
        context: ExtensionContext,
        throwable: Throwable
    ) {
        failureCount.addOne()
        val successCount = -failureCount + runsTotal
        if ((successCount) < minSuccessTries) {
            throw NotEnoughSuccessfulRunsException(
                "Cannot hit success percentage of " +
                        "$successPercentage% - ${(failureCount.toDouble() / runsTotal * 100).toInt()}% " +
                        "of failures already",
                throwable
            )
        } else if (-failureCount + rerunNum < minSuccessTries) {
            throw TestAbortedException(
                "run $rerunNum failed",
                throwable
            )
        }
    }
}