package lesson4

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag

class OpenAddressingSetTest {

    private fun createOpenAddressingSet(): MutableSet<String> = OpenAddressingSet(5)

    @Test
    @Tag("Example")
    fun add() {
        val set = OpenAddressingSet<String>(16)
        assertTrue(set.isEmpty())
        set.add("Alpha")
        set.add("Beta")
        set.add("Omega")
        assertSame(3, set.size)
        assertTrue("Beta" in set)
        assertFalse("Gamma" in set)
        assertTrue("Omega" in set)
    }

    @Test
    @Tag("Example")
    fun remove() {
        val set = OpenAddressingSet<String>(3)
        assertTrue(set.isEmpty())
        set.add("Alpha")
        set.add("Beta")
        set.add("Omega")
        set.remove("Alpha")
        assertSame(2, set.size)
        assertFalse("Alpha" in set)
        assertTrue("Beta" in set)
        assertTrue("Omega" in set)
        set.add("Alpha")
        assertTrue("Alpha" in set)
        assertTrue("Beta" in set)
        assertTrue("Omega" in set)
    }

    @Test
    @Tag("Example")
    fun iterator() {
        val set = OpenAddressingSet<String>(3)
        assertTrue(set.isEmpty())
        set.add("Alpha")
        set.add("Beta")
        set.add("Omega")
        assertEquals(setOf("Alpha", "Beta", "Omega"), set)
        val iterator = set.iterator()
        iterator.next()
        iterator.remove()
        assertEquals(2, set.size)
        iterator.next()
        iterator.remove()
        assertEquals(1, set.size)
        iterator.next()
        iterator.remove()
        assertEquals(0, set.size)
        assertFalse(set.contains("Alpha"))
        assertFalse(set.contains("Beta"))
        assertFalse(set.contains("Omega"))
    }

    @Test
    @Tag("Example")
    fun testIterator() {
        testIterator { createOpenAddressingSet() }
    }

    @Test
    @Tag("Example")
    fun testIteratorRemove() {
        testIteratorRemove { createOpenAddressingSet() }
    }

    @Test
    @Tag("Example")
    fun testIteratorExceptions() {
        testIteratorRemove { createOpenAddressingSet() }
    }
}