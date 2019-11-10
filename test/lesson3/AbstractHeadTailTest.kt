package lesson3

import java.util.*
import kotlin.test.*

abstract class AbstractHeadTailTest {
    private lateinit var tree: SortedSet<Int>
    private lateinit var randomTree: SortedSet<Int>
    private val randomTreeSize = 1000
    private val randomValues = mutableListOf<Int>()

    protected fun fillTree(create: () -> SortedSet<Int>) {
        this.tree = create()
        //В произвольном порядке добавим числа от 1 до 10
        tree.add(5)
        tree.add(1)
        tree.add(2)
        tree.add(7)
        tree.add(9)
        tree.add(10)
        tree.add(8)
        tree.add(4)
        tree.add(3)
        tree.add(6)

        this.randomTree = create()
        val random = Random()
        for (i in 0 until randomTreeSize) {
            val randomValue = random.nextInt(randomTreeSize) + 1
            if (randomTree.add(randomValue)) {
                randomValues.add(randomValue)
            }
        }
    }


    protected fun doHeadSetTest() {
        var set: SortedSet<Int> = tree.headSet(5)
        assertEquals(true, set.contains(1))
        assertEquals(true, set.contains(2))
        assertEquals(true, set.contains(3))
        assertEquals(true, set.contains(4))
        assertEquals(false, set.contains(5))
        assertEquals(false, set.contains(6))
        assertEquals(false, set.contains(7))
        assertEquals(false, set.contains(8))
        assertEquals(false, set.contains(9))
        assertEquals(false, set.contains(10))


        set = tree.headSet(127)
        for (i in 1..10)
            assertEquals(true, set.contains(i))

    }

    protected fun doTailSetTest() {
        var set: SortedSet<Int> = tree.tailSet(5)
        assertEquals(false, set.contains(1))
        assertEquals(false, set.contains(2))
        assertEquals(false, set.contains(3))
        assertEquals(false, set.contains(4))
        assertEquals(true, set.contains(5))
        assertEquals(true, set.contains(6))
        assertEquals(true, set.contains(7))
        assertEquals(true, set.contains(8))
        assertEquals(true, set.contains(9))
        assertEquals(true, set.contains(10))

        set = tree.tailSet(-128)
        for (i in 1..10)
            assertEquals(true, set.contains(i))

    }

    protected fun doHeadSetRelationTest() {
        val set: SortedSet<Int> = tree.headSet(7)
        assertEquals(6, set.size)
        assertEquals(10, tree.size)
        tree.add(0)
        assertTrue(set.contains(0))
        set.add(-2)
        assertTrue(tree.contains(-2))
        tree.add(12)
        assertFalse(set.contains(12))
        assertFailsWith<IllegalArgumentException> { set.add(8) }
        assertEquals(8, set.size)
        assertEquals(13, tree.size)
    }

    protected fun doTailSetRelationTest() {
        val set: SortedSet<Int> = tree.tailSet(4)
        assertEquals(7, set.size)
        assertEquals(10, tree.size)
        tree.add(12)
        assertTrue(set.contains(12))
        set.add(42)
        assertTrue(tree.contains(42))
        tree.add(0)
        assertFalse(set.contains(0))
        assertFailsWith<IllegalArgumentException> { set.add(-2) }
        assertEquals(9, set.size)
        assertEquals(13, tree.size)
    }

    protected fun doSubSetTest() {
        val smallSet: SortedSet<Int> = tree.subSet(3, 8)
        assertEquals(false, smallSet.contains(1))
        assertEquals(false, smallSet.contains(2))
        assertEquals(true, smallSet.contains(3))
        assertEquals(true, smallSet.contains(4))
        assertEquals(true, smallSet.contains(5))
        assertEquals(true, smallSet.contains(6))
        assertEquals(true, smallSet.contains(7))
        assertEquals(false, smallSet.contains(8))
        assertEquals(false, smallSet.contains(9))
        assertEquals(false, smallSet.contains(10))

        assertFailsWith<IllegalArgumentException> { smallSet.add(2) }
        assertFailsWith<IllegalArgumentException> { smallSet.add(9) }

        val allSet = tree.subSet(-128, 128)
        for (i in 1..10)
            assertEquals(true, allSet.contains(i))

        val random = Random()
        val toElement = random.nextInt(randomTreeSize) + 1
        val fromElement = random.nextInt(toElement - 1) + 1

        val randomSubset = randomTree.subSet(fromElement, toElement)
        randomValues.forEach { element ->
            assertEquals(element in fromElement until toElement, randomSubset.contains(element))
        }

        val tree = BinaryTree<Int>().subSet(0, 21)
        assertEquals(0, tree.size)
        assertFalse(tree.contains(5))
        tree.add(10)
        tree.add(5)
        tree.add(7)
        tree.add(10)
        assertEquals(3, tree.size)
        assertTrue(tree.contains(5))
        tree.add(3)
        tree.add(1)
        tree.add(3)
        tree.add(4)
        assertEquals(6, tree.size)
        assertFalse(tree.contains(8))
        tree.add(8)
        tree.add(15)
        tree.add(15)
        tree.add(20)
        assertEquals(9, tree.size)
        assertTrue(tree.contains(8))
        assertTrue((tree as BinaryTree).checkInvariant())
        assertEquals(1, tree.first())
        assertEquals(20, tree.last())
    }

    protected fun doSubRemoveTest() {
        val random = Random()
        for (iteration in 1..100) {
            val list = mutableListOf<Int>()
            for (i in 1..20) {
                list.add(random.nextInt(100))
            }
            val binarySet = BinaryTree<Int>().subSet(0, 101)
            assertFalse(binarySet.remove(42))
            for (element in list) {
                binarySet += element
            }
//            val originalHeight = binarySet.height()
            val toRemove = list[random.nextInt(list.size)]
            val oldSize = binarySet.size
            assertTrue(binarySet.remove(toRemove))
            assertEquals(oldSize - 1, binarySet.size)
            println("Removing $toRemove from $list")
            for (element in list) {
                val inn = element != toRemove
                assertEquals(
                    inn, element in binarySet,
                    "$element should be ${if (inn) "in" else "not in"} tree"
                )
            }
//            assertTrue(binarySet.checkInvariant(), "Binary tree invariant is false after tree.remove()")
//            assertTrue(
//                binarySet.height() <= originalHeight,
//                "After removal of $toRemove from $list binary tree height increased"
//            )
        }
    }

    protected fun doSubIteratorRemoveTest() {
        val random = Random()
        for (iteration in 1..100) {
            val list = mutableListOf<Int>()
            for (i in 1..20) {
                list.add(random.nextInt(100))
            }
            val treeSet = TreeSet<Int>()
            val binarySet = BinaryTree<Int>().subSet(0, 101)
            for (element in list) {
                treeSet += element
                binarySet += element
            }
            val toRemove = list[random.nextInt(list.size)]
            treeSet.remove(toRemove)
            println("Removing $toRemove from $list")
            val iterator = binarySet.iterator()
            var counter = binarySet.size
            while (iterator.hasNext()) {
                val element = iterator.next()
                counter--
                print("$element ")
                if (element == toRemove) {
                    iterator.remove()
                }
            }
            assertEquals(
                0, counter,
                "Iterator.remove() of $toRemove from $list changed iterator position: " +
                        "we've traversed a total of ${binarySet.size - counter} elements instead of ${binarySet.size}"
            )
            println()
            assertEquals<SortedSet<*>>(treeSet, binarySet, "After removal of $toRemove from $list")
            assertEquals(treeSet.size, binarySet.size, "Size is incorrect after removal of $toRemove from $list")
            for (element in list) {
                val inn = element != toRemove
                assertEquals(
                    inn, element in binarySet,
                    "$element should be ${if (inn) "in" else "not in"} tree"
                )
            }
//            assertTrue(binarySet.checkInvariant(), "Binary tree invariant is false after tree.iterator().remove()")
        }
    }

    protected fun doSubIteratorTest() {
        val random = Random()
        for (iteration in 1..100) {
            val list = mutableListOf<Int>()
            for (i in 1..20) {
                list.add(random.nextInt(100))
            }
            val treeSet = TreeSet<Int>()
            val binarySet = BinaryTree<Int>().subSet(0, 101)
            assertFalse(binarySet.iterator().hasNext(), "Iterator of empty list should not have next element")
            for (element in list) {
                treeSet += element
                binarySet += element
            }
            val treeIt = treeSet.iterator()
            val binaryIt = binarySet.iterator()
            println("Traversing $list")
            while (treeIt.hasNext()) {
                assertEquals(treeIt.next(), binaryIt.next(), "Incorrect iterator state while iterating $treeSet")
            }
            val iterator1 = binarySet.iterator()
            val iterator2 = binarySet.iterator()
            println("Consistency check for hasNext $list")
            // hasNext call should not affect iterator position
            while (iterator1.hasNext()) {
                assertEquals(
                    iterator2.next(), iterator1.next(),
                    "Call of iterator.hasNext() changes its state while iterating $treeSet"
                )
            }
        }
    }

    protected fun doSubSetRelationTest() {
        val set: SortedSet<Int> = tree.subSet(2, 15)
        assertEquals(9, set.size)
        assertEquals(10, tree.size)
        tree.add(11)
        assertTrue(set.contains(11))
        set.add(14)
        assertTrue(tree.contains(14))
        tree.add(0)
        assertFalse(set.contains(0))
        tree.add(15)
        assertFalse(set.contains(15))
        assertFailsWith<IllegalArgumentException> { set.add(1) }
        assertFailsWith<IllegalArgumentException> { set.add(20) }
        assertEquals(11, set.size)
        assertEquals(14, tree.size)
    }

}