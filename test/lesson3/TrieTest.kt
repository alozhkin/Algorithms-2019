package lesson3

import lesson4.testIterator
import lesson4.testIteratorExceptions
import lesson4.testIteratorRemove
import org.junit.jupiter.api.Tag
import java.lang.StringBuilder
import kotlin.test.*

class TrieTest {

    private fun createTrie(): MutableSet<String> = Trie()

    @Test
    @Tag("Example")
    fun generalTest() {
        val trie = Trie()
        assertEquals(0, trie.size)
        assertFalse("Some" in trie)
        trie.add("abcdefg")
        assertTrue("abcdefg" in trie)
        assertFalse("abcdef" in trie)
        assertFalse("a" in trie)
        assertFalse("g" in trie)

        trie.add("zyx")
        trie.add("zwv")
        trie.add("zyt")
        trie.add("abcde")
        assertEquals(5, trie.size)
        assertTrue("abcdefg" in trie)
        assertFalse("abcdef" in trie)
        assertTrue("abcde" in trie)
        assertTrue("zyx" in trie)
        assertTrue("zyt" in trie)
        assertTrue("zwv" in trie)
        assertFalse("zy" in trie)
        assertFalse("zv" in trie)

        trie.remove("zwv")
        trie.remove("zy")
        assertEquals(4, trie.size)
        assertTrue("zyt" in trie)
        assertFalse("zwv" in trie)

        trie.clear()
        assertEquals(0, trie.size)
        assertFalse("zyx" in trie)
    }

    @Test
    @Tag("Example")
    fun addTwice() {
        val trie = Trie()
        val sb = StringBuilder()
        val str = "UZUMYMW"
        sb.append("UZUMYMW")
        assertNotSame(str, sb.toString())
        trie.add(str)
        trie.add(sb.toString())
        assertEquals(1, trie.size)
    }

    @Test
    @Tag("Hard")
    fun rudeIteratorTest() {
        val trie = Trie()
        assertEquals(setOf<String>(), trie)
        trie.add("abcdefg")
        trie.add("zyx")
        trie.add("zwv")
        trie.add("zyt")
        trie.add("abcde")

        assertEquals(setOf("abcdefg", "zyx", "zwv", "zyt", "abcde"), trie)
    }

    @Test
    @Tag("Hard")
    fun rudeIteratorRemoveTest() {
        val trie = Trie()
        assertEquals(setOf<String>(), trie)
        trie.add("1")
        trie.add("99")
        trie.add("96")
        trie.add("9")
        trie.add("9")
        val iter = trie.iterator()
        iter.next()
        iter.remove()
        iter.next()
        iter.remove()
        iter.next()
        iter.remove()
        iter.next()
        iter.remove()
        assertEquals(0, trie.size)
    }

    @Test
    @Tag("Hard")
    fun testIterator() {
        testIterator { createTrie() }
    }

    @Test
    @Tag("Hard")
    fun testIteratorRemove() {
        testIteratorRemove { createTrie() }
    }

    @Test
    @Tag("Example")
    fun testIteratorExceptions() {
        testIteratorExceptions { createTrie() }
    }
}