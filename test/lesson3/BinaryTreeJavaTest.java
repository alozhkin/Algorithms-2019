package lesson3;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class BinaryTreeJavaTest {
    @Test
    public void removeTest() {
        BinaryTree<Integer> tree = new BinaryTree<>();
        Assert.assertFalse(tree.remove(8));
        tree.add(5);
        tree.add(6);
        tree.add(7);
        Assertions.assertThrows(ClassCastException.class, () ->  tree.remove(5.0));
        Assertions.assertThrows(NullPointerException.class, () ->  tree.remove(null));

        SortedSet<Integer> subSet = tree.subSet(0, 4);
        Assertions.assertThrows(IllegalArgumentException.class, () ->  subSet.remove(7));
        Assert.assertEquals(3, tree.size());
        Assert.assertEquals(0, subSet.size());

        Assertions.assertThrows(ClassCastException.class, () ->  subSet.remove(5.0));
        Assertions.assertThrows(NullPointerException.class, () ->  subSet.remove(null));

        SortedSet<Integer> subSet2 = tree.subSet(0, 8);
        SortedSet<Integer> headSet = tree.headSet(6);
        SortedSet<Integer> tailSet = tree.tailSet(7);

        headSet.remove(5);
        Assert.assertEquals(2, tree.size());
        Assert.assertEquals(2, subSet2.size());
        Assert.assertEquals(1, tailSet.size());

        tailSet.remove(7);
        Assert.assertEquals(1, tree.size());
        Assert.assertEquals(1, subSet2.size());

        subSet2.remove(6);
        Assert.assertEquals(0, tree.size());
        Assert.assertEquals(0, headSet.size());
        Assert.assertEquals(0, tailSet.size());
        Assert.assertEquals(0, subSet2.size());
    }

    @Test
    public void addTest() {
        BinaryTree<Integer> tree = new BinaryTree<>();
        Assertions.assertThrows(NullPointerException.class, () ->  tree.add(null));
        tree.add(8);
        tree.add(7);
        tree.add(6);

        SortedSet<Integer> subSet = tree.subSet(0, 4);
        Assertions.assertThrows(IllegalArgumentException.class, () ->  subSet.add(7));
        Assert.assertEquals(3, tree.size());
        Assert.assertEquals(0, subSet.size());

        Assertions.assertThrows(NullPointerException.class, () ->  subSet.add(null));

        SortedSet<Integer> headSet = tree.headSet(5);
        SortedSet<Integer> tailSet = tree.tailSet(9);

        headSet.add(3);
        Assert.assertEquals(4, tree.size());
        Assert.assertEquals(1, subSet.size());
        Assert.assertEquals(0, tailSet.size());

        subSet.add(2);
        Assert.assertEquals(5, tree.size());
        Assert.assertEquals(2, headSet.size());
        Assert.assertEquals(0, tailSet.size());

        tailSet.add(22);
        Assert.assertEquals(6, tree.size());
        Assert.assertEquals(2, headSet.size());
        Assert.assertEquals(2, subSet.size());
    }

    @Test
    public void firstLastTest() {
        BinaryTree<Integer> tree = new BinaryTree<>();
        tree.add(8);
        tree.add(7);
        tree.add(6);
        Assert.assertEquals(Integer.valueOf(6), tree.first());
        Assert.assertEquals(Integer.valueOf(8), tree.last());

        SortedSet<Integer> headSet = tree.headSet(6);
        Assert.assertEquals(0, headSet.size());
        Assertions.assertThrows(NoSuchElementException.class, headSet::first);
        Assertions.assertThrows(NoSuchElementException.class, headSet::last);


        SortedSet<Integer> tailSet = tree.tailSet(9);
        Assert.assertEquals(0, tailSet.size());
        Assertions.assertThrows(NoSuchElementException.class, tailSet::first);
        Assertions.assertThrows(NoSuchElementException.class, tailSet::last);

        SortedSet<Integer> subSet = tree.subSet(10, 15);
        Assert.assertEquals(0, subSet.size());
        Assertions.assertThrows(NoSuchElementException.class, subSet::first);
        Assertions.assertThrows(NoSuchElementException.class, subSet::last);

        tree.add(10);
        Assert.assertEquals(1, tailSet.size());
        Assert.assertEquals(1, subSet.size());
        Assert.assertEquals(Integer.valueOf(10), tailSet.first());
        Assert.assertEquals(Integer.valueOf(10), tailSet.last());
        Assert.assertEquals(Integer.valueOf(10), subSet.first());
        Assert.assertEquals(Integer.valueOf(10), subSet.last());

        tree.add(11);
        Assert.assertEquals(2, tailSet.size());
        Assert.assertEquals(2, subSet.size());
        Assert.assertEquals(Integer.valueOf(10), tailSet.first());
        Assert.assertEquals(Integer.valueOf(11), tailSet.last());
        Assert.assertEquals(Integer.valueOf(10), subSet.first());
        Assert.assertEquals(Integer.valueOf(11), subSet.last());

        tree.add(5);
        Assert.assertEquals(1, headSet.size());
        Assert.assertEquals(Integer.valueOf(5), headSet.first());
        Assert.assertEquals(Integer.valueOf(5), headSet.last());

        tree.add(4);
        Assert.assertEquals(2, headSet.size());
        Assert.assertEquals(Integer.valueOf(4), headSet.first());
        Assert.assertEquals(Integer.valueOf(5), headSet.last());
    }

    @Test
    public void iteratorTest() {
        BinaryTree<Integer> tree = new BinaryTree<>();
        SortedSet<Integer> subSet = tree.subSet(1, 3);
        SortedSet<Integer> headSet = tree.headSet(3);
        SortedSet<Integer> tailSet = tree.tailSet(2);
        testIteratorExceptions(tree, subSet);
        testIteratorExceptions(tree, headSet);
        testIteratorExceptions(tree, tailSet);
    }

    private void testIteratorExceptions(BinaryTree<Integer> tree, SortedSet<Integer> subSet) {
        tree.add(8);
        tree.add(7);
        tree.add(6);

        Iterator it = tree.iterator();
        tree.add(3);
        Assertions.assertThrows(ConcurrentModificationException.class, it::next);

        it = tree.iterator();
        tree.remove(3);
        Assertions.assertThrows(ConcurrentModificationException.class, it::next);
        Assertions.assertDoesNotThrow(it::hasNext);

        it = tree.iterator();
        it.next();
        Assertions.assertDoesNotThrow(it::remove);
        it.next();
        it.next();
        Assertions.assertThrows(NoSuchElementException.class, it::next);

        it = tree.iterator();
        it.next();
        it.remove();
        Assertions.assertThrows(IllegalStateException.class, it::remove);
        it.next();
        it.remove();

        tree.add(1);
        tree.add(2);
        tree.add(3);

        it = tree.iterator();
        Iterator subSetIt = subSet.iterator();
        it.next();
        subSetIt.next();
        it.remove();
        Assertions.assertThrows(ConcurrentModificationException.class, subSetIt::next);
        Assert.assertFalse(subSet.contains(1));
        subSetIt = subSet.iterator();
        subSetIt.next();
        subSetIt.remove();
        Assertions.assertThrows(ConcurrentModificationException.class, it::next);
        Assert.assertFalse(tree.contains(2));
        it = tree.iterator();
        it.next();
        it.remove();
        Assert.assertEquals(0, subSet.size());
    }
}
