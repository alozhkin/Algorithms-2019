package lesson3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T extends Comparable<T>> implements Comparable<Node<T>> {
        T value;

        Node<T> left = null;

        Node<T> right = null;

        Node<T> parent = null;

        Node(T value) {
            this.value = value;
        }

        Node<T> minimum() {
            Node<T> current = this;
            while (current.left != null) {
                current = current.left;
            }
            return current;
        }

        Node<T> maximum() {
            Node<T> current = this;
            while (current.right != null) {
                current = current.right;
            }
            return current;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(value, node.value) &&
                    Objects.equals(left, node.left) &&
                    Objects.equals(right, node.right) &&
                    Objects.equals(parent, node.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, left, right, parent);
        }

        @Override
        public int compareTo(@NotNull Node<T> o) {
            return this.value.compareTo(o.value);
        }
    }

    private Node<T> root = null;

    protected int size = 0;

    private int modCount = 0;

    @Override
    public boolean add(T t) {
        if (t == null) throw new NullPointerException();
        Node<T> closest = findClosest(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        }
        else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
            newNode.parent = closest;
        }
        else {
            assert closest.right == null;
            closest.right = newNode;
            newNode.parent = closest;
        }
        size++;
        modCount++;
        return true;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    public int height() {
        return height(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    @Override
    public boolean remove(Object o) {
        T value = (T) o;
        Node<T> node = find(value);
        return removeNode(node);
    }
    /*
    Память: O(1)
    Сложность: O(lgn)
     */

    private boolean removeNode(Node<T> node) {
        if (node == null) return false;
        if (node.left == null) {
            transplant(node, node.right);
        } else if (node.right == null) {
            transplant(node, node.left);
        } else {
            Node<T> y = node.right.minimum();
            if (y.parent != node) {
                transplant(y, y.right);
                y.right = node.right;
                y.right.parent = y;
            }
            transplant(node, y);
            y.left = node.left;
            y.left.parent = y;
        }
        size--;
        modCount++;
        return true;
    }

    private void transplant(Node<T> to, Node<T> from) {
        if (to.parent == null) {
            root = from;
        } else if (to.equals(to.parent.left)) {
            to.parent.left = from;
        } else {
            to.parent.right = from;
        }
        if (from != null) {
            from.parent = to.parent;
        }
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null;
    }

    private Node<T> findEqualsOrBigger(T value) {
        if (root == null) return null;
        return findEqualsOrBigger(root, value);
    }

    private Node<T> findEqualsOrBigger(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return start;
            return findEqualsOrBigger(start.left, value);
        }
        else {
            if (start.right == null) return null;
            return findEqualsOrBigger(start.right, value);
        }
    }

    private Node<T> findEqualsOrSmaller(T value) {
        if (root == null) return null;
        return findEqualsOrSmaller(root, value);
    }

    private Node<T> findEqualsOrSmaller(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return null;
            return findEqualsOrSmaller(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return findEqualsOrSmaller(start.right, value);
        }
    }

    private Node<T> findClosest(T value) {
        if (root == null) return null;
        return findClosest(root, value);
    }

    private Node<T> findClosest(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return start;
            return findClosest(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return findClosest(start.right, value);
        }
    }

    private Node<T> find(T value) {
        return find(root, value);
    }

    private Node<T> find(Node<T> node, T value) {
        while (node != null && !value.equals(node.value)) {
            if (value.compareTo(node.value) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node;
    }

    private Node<T> findNext(Node<T> x) {
        if (root == null) return null;
        if (x == null) return root.minimum();
        if (x.right != null) {
            return x.right.minimum();
        }
        Node<T> y = x.parent;
        while (y != null && x == y.right) {
            x = y;
            y = y.parent;
        }
        return y;
    }

    private Node<T> findPrevious(Node<T> x) {
        if (root == null) return null;
        if (x == null) return root.maximum();
        if (x.left != null) {
            return x.left.maximum();
        }
        Node<T> y = x.parent;
        while (y != null && x == y.left) {
            x = y;
            y = y.parent;
        }
        return y;
    }

    public class BinaryTreeIterator implements Iterator<T> {

        private Node<T> current;
        private Node<T> prev = null;
        private int expectedModCount;

        private BinaryTreeIterator() {
            current = findNext(null);
            expectedModCount = modCount;
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }
        /*
        Память: O(1)
        Сложность: O(1)
         */

        /**
         * Поиск следующего элемента
         * Средняя
         */
        @Override
        public T next() {
            if (modCount != expectedModCount) throw new ConcurrentModificationException();
            prev = current;
            current = findNext(current);
            if (prev == null) throw new NoSuchElementException();
            return prev.value;
        }
        /*
        Память: O(1)
        Сложность: O(lgn)
         */

        /**
         * Удаление следующего элемента
         * Сложная
         */
        @Override
        public void remove() {
            if (modCount != expectedModCount) throw new ConcurrentModificationException();
            if (prev == null) throw new IllegalStateException();
            BinaryTree.this.removeNode(prev);
            expectedModCount = modCount;
            prev = null;
        }
        /*
        Память: O(1)
        Сложность: O(lgn)
         */
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    private SubSet subSet;

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        subSet = new SubSet(this, false, fromElement, true, false, toElement, false);
        return subSet;
    }
    /*
    subSet, так же как и headSet и tailSet создаются за O(1) и занимают O(1)
     */

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        subSet = new SubSet(this, true, null, false, false, toElement, false);
        return subSet;
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        subSet = new SubSet(this, false, fromElement, true, true, null, false);
        return subSet;
    }

    class SubSet extends BinaryTree<T> {
        private final T lo, hi;
        private final boolean loInclusive, hiInclusive;
        private final boolean fromStart, toEnd;
        private BinaryTree<T> parent;

        SubSet(BinaryTree<T> parent,
               boolean fromStart, T lo, boolean loInclusive,
               boolean toEnd,     T hi, boolean hiInclusive) {
            if (!fromStart && !toEnd) {
                if (lo.compareTo(hi) > 0)
                    throw new IllegalArgumentException("fromKey > toKey");
            }
            this.parent = parent;
            this.lo = lo;
            this.loInclusive = loInclusive;
            this.hi = hi;
            this.hiInclusive = hiInclusive;
            this.fromStart = fromStart;
            this.toEnd = toEnd;
        }

        @Override
        public boolean contains(Object o) {
            if (!inRange((T) o)) return false;
            return parent.contains(o);
        }

        @Override
        public boolean add(T t) {
            if (!inRange(t)) throw new IllegalArgumentException();
            return parent.add(t);
        }

        @Override
        public boolean remove(Object o) {
            if (!inRange((T) o)) throw new IllegalArgumentException();
            return parent.remove(o);
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new SubSetIterator();
        }

        @Override
        public int size() {
            Iterator it = iterator();
            int count = 0;
            while (it.hasNext()) {
                count++;
                it.next();
            }
            size = count;
            return count;
        }

        @Override
        public T first() {
            Node<T> node = findFirst();
            if (node == null) throw new NoSuchElementException();
            return node.value;
        }

        private Node<T> findFirst() {
            if (fromStart) {
                return check(parent.firstNode());
            } else {
                Node<T> node = parent.findEqualsOrBigger(lo);
                if (node == null) return null;
                if (loInclusive) {
                    return check(node);
                } else {
                    if (node.value.equals(lo)) {
                        return check(parent.findNext(node));
                    } else {
                        return check(node);
                    }
                }
            }
        }

        @Override
        public T last() {
            Node<T> node = findLast();
            if (node == null) throw new NoSuchElementException();
            return node.value;
        }

        private Node<T> findLast() {
            if (toEnd) {
                return check(parent.lastNode());
            } else {
                Node<T> node = parent.findEqualsOrSmaller(hi);
                if (node == null) return null;
                if (hiInclusive) {
                    return check(node);
                } else {
                    if (node.value.equals(hi)) {
                        return check(parent.findPrevious(node));
                    } else {
                        return check(node);
                    }
                }
            }
        }

        private Node<T> check(Node<T> node) {
            if (node == null) return null;
            if (inRange(node.value)) {
                return node;
            } else {
                return null;
            }
        }

        private boolean tooLow(T key) {
            if (!fromStart) {
                int c = key.compareTo(lo);
                return c < 0 || (c == 0 && !loInclusive);
            }
            return false;
        }

        private boolean tooHigh(T key) {
            if (!toEnd) {
                int c = key.compareTo(hi);
                return c > 0 || (c == 0 && !hiInclusive);
            }
            return false;
        }

        private boolean inRange(T key) {
            return !tooLow(key) && !tooHigh(key);
        }

        private class SubSetIterator extends BinaryTreeIterator {
            private Node<T> last;
            private Node<T> current;
            private Node<T> prev = null;
            private int expectedModCount;

            private SubSetIterator() {
                Node<T> first = findFirst();
                if (first == null) {
                    last = null;
                } else {
                    last = findLast();
                }
                current = first;
                expectedModCount = parent.modCount;
            }

            @Override
            public boolean hasNext() {
                return current != null && last != null && current.compareTo(last) <= 0;
            }

            @Override
            public T next() {
                if (parent.modCount != expectedModCount) throw new ConcurrentModificationException();
                if (prev != null && prev.equals(last)) {
                    throw new NoSuchElementException();
                }
                prev = current;
                if (prev == null) throw new NoSuchElementException();
                current = BinaryTree.this.findNext(current);
                return prev.value;
            }

            @Override
            public void remove() {
                if (parent.modCount != expectedModCount) throw new ConcurrentModificationException();
                if (prev == null) throw new IllegalStateException();
                BinaryTree.this.removeNode(prev);
                prev = null;
                expectedModCount = parent.modCount;
            }
        }
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    private Node<T> firstNode() {
        if (root == null) return null;
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    private Node<T> lastNode() {
        if (root == null) return null;
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current;
    }
}