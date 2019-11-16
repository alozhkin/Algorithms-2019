package lesson7.annotations

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import org.junit.platform.commons.support.AnnotationSupport
import java.util.stream.IntStream
import java.util.stream.Stream

class RerunTestTemplateExtension : TestTemplateInvocationContextProvider {
    override fun provideTestTemplateInvocationContexts(context: ExtensionContext): Stream<TestTemplateInvocationContext> {
        val annotation = AnnotationSupport.findAnnotation(context.testMethod.get(), ReRun::class.java).get()
        check(annotation)

        val failureCount = MutableInt(0)
        return IntStream.rangeClosed(1, annotation.invocationCount)
            .mapToObj { RerunContext(it, annotation.invocationCount, annotation.successPercentage, failureCount) }
    }

    private fun check(annotation: ReRun) {
        if (annotation.invocationCount < 1) {
            throw IllegalRerunArgumentException("Invocation count was ${annotation.invocationCount}")
        }
        if (annotation.successPercentage > 100 || annotation.successPercentage < 0) {
            throw IllegalRerunArgumentException("Success percentage was ${annotation.successPercentage}")
        }
    }

    override fun supportsTestTemplate(context: ExtensionContext): Boolean {
        return context.testMethod.map { it.isAnnotationPresent(ReRun::class.java) }.orElse(false)
    }
}