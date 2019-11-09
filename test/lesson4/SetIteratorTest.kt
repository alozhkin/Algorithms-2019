package lesson4

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.NoSuchElementException

fun testIterator(create: () -> MutableSet<String>) {
    val random = Random()
    for (iteration in 1..100) {
        val list = mutableListOf<String>()
        for (i in 1..20) {
            list.add(random.nextInt(100).toString())
        }
        val controlSet = mutableSetOf<String>()
        val actualSet = create()
        kotlin.test.assertFalse(
            actualSet.iterator().hasNext(),
            "Iterator of empty set should not have next element"
        )
        for (element in list) {
            controlSet += element
            actualSet += element
        }
        println("Traversing $list")
        val iterator1 = actualSet.iterator()
        val iterator2 = actualSet.iterator()
        println("Consistency check for hasNext $list")
        // hasNext call should not affect iterator position
        while (iterator1.hasNext()) {
            kotlin.test.assertEquals(
                iterator2.next(), iterator1.next(),
                "Call of iterator.hasNext() changes its state while iterating $controlSet"
            )
        }
    }
}

fun testIteratorRemove(create: () -> MutableSet<String>) {
    val random = Random()
    for (iteration in 1..100) {
        val list = mutableListOf<String>()
        for (i in 1..20) {
            list.add(random.nextInt(100).toString())
        }
        val controlSet = mutableSetOf<String>()
        val actualSet = create()
        for (element in list) {
            controlSet += element
            actualSet += element
        }
        val toRemove = list[random.nextInt(list.size)]
        controlSet.remove(toRemove)
        println("Removing $toRemove from $list")
        val iterator = actualSet.iterator()
        var counter = actualSet.size
        while (iterator.hasNext()) {
            val element = iterator.next()
            counter--
            print("$element ")
            if (element == toRemove) {
                iterator.remove()
            }
        }
        kotlin.test.assertEquals(
            0, counter,
            "Iterator.remove() of $toRemove from $list changed iterator position: " +
                    "we've traversed a total of ${actualSet.size - counter} elements instead of ${actualSet.size}"
        )
        println()
        kotlin.test.assertEquals<MutableSet<*>>(controlSet, actualSet, "After removal of $toRemove from $list")
        kotlin.test.assertEquals(
            controlSet.size,
            actualSet.size,
            "Size is incorrect after removal of $toRemove from $list"
        )
        for (element in list) {
            val inn = element != toRemove
            kotlin.test.assertEquals(
                inn, element in actualSet,
                "$element should be ${if (inn) "in" else "not in"} tree"
            )
        }
    }
}

fun testIteratorExceptions(create: () -> MutableSet<String>) {
    val set = create()
    set.add("8")
    set.add("7")
    set.add("6")

    var iterator: MutableIterator<*> = set.iterator()
    assertThrows<IllegalStateException> { iterator.remove() }

    iterator = set.iterator()
    iterator.next()
    set.add("3")
    assertThrows<ConcurrentModificationException> { iterator.next() }
    assertThrows<ConcurrentModificationException> { iterator.remove() }
    assertDoesNotThrow { iterator.hasNext() }

    iterator = set.iterator()
    iterator.next()
    set.remove("3")
    assertThrows<ConcurrentModificationException> { iterator.next() }
    assertThrows<ConcurrentModificationException> { iterator.remove() }
    assertDoesNotThrow { iterator.hasNext() }

    iterator = set.iterator()
    iterator.next()
    assertDoesNotThrow { iterator.remove() }
    iterator.next()
    iterator.next()
    assertThrows<NoSuchElementException> { iterator.next() }

    iterator = set.iterator()
    iterator.next()
    iterator.remove()
    assertThrows<IllegalStateException> { iterator.remove() }
    iterator.next()
    iterator.remove()
}
