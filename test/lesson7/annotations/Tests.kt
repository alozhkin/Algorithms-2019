package lesson7.annotations

import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@Disabled
class Tests {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test1 {
        private var count = 0

        @ReRun(10, 90)
        @TestTemplate
        fun test1() {
            count++
            if (count == 8) assertEquals(2, 3)
            println(count)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test2 {
        @ReRun(10, 90)
        @TestTemplate
        fun test2() {
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test3 {
        private var count = 0

        @ReRun(10, 90)
        @TestTemplate
        fun test3() {
            count++
            if (count == 2 || count == 3) assertEquals(2, 3)
            println(count)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test4 {
        private var count = 0

        @ReRun(10, 90)
        @TestTemplate
        fun test4() {
            count++
            if (count == 9 || count == 10) assertEquals(2, 3)
            println(count)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test5 {
        private var count = 0

        @ReRun(10, 80)
        @TestTemplate
        fun test5() {
            count++
            if (count == 9 || count == 10) assertEquals(2, 3)
            println(count)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test6 {
        private var count = 0

        @ReRun(10, 10)
        @TestTemplate
        fun test6() {
            count++
            if (count != 9) assertEquals(2, 3)
            println(count)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test7 {
        private var count = 0

        @ReRun(10, 10)
        @TestTemplate
        fun test7() {
            count++
            if (count != 1) assertEquals(2, 3)
            println(count)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test8 {
        private var count = 0

        @ReRun(10, 101)
        @TestTemplate
        fun test8() {
            count++
            if (count != 1) assertEquals(2, 3)
            println(count)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test9 {
        private var count = 0

        @ReRun(10, -1)
        @TestTemplate
        fun test9() {
            count++
            if (count != 1) assertEquals(2, 3)
            println(count)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class Test10 {
        private var count = 0

        @ReRun(-1, 100)
        @TestTemplate
        fun test10() {
            count++
            if (count != 1) assertEquals(2, 3)
            println(count)
        }
    }
}