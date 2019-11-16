package lesson7.annotations

import org.junit.jupiter.api.extension.ExtendWith

@Target(AnnotationTarget.FUNCTION)
@ExtendWith(RerunTestTemplateExtension::class)
annotation class ReRun(val invocationCount: Int, val successPercentage: Int = 100)
