package lesson3;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        final T value;

        Node<T> left = null;

        Node<T> right = null;

        Node<T> parent = null;

        Node(T value) {
            this.value = value;
        }

        public Node<T> minimum() {
            if (this == null) throw new NoSuchElementException();
            Node<T> current = this;
            while (current.left != null) {
                current = current.left;
            }
            return current;
        }
        public Node<T> maximum() {
            if (this == null) throw new NoSuchElementException();
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
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
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
            newNode.parent = closest.left;
        }
        else {
            assert closest.right == null;
            closest.right = newNode;
            newNode.parent = closest.right;
        }
        size++;
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
        Node<T> node = find((T) o);
        while (true) {
            if (node == null) return false;
            if (node.left == null) {
                transplant(node, node.right);
            } else if (node.right == null) {
                transplant(node, node.left);
            } else {
                Node<T> y = node.right.minimum();
                if (y.parent != null) {
                    transplant(y, y.right);
                    y.right = node.right;
                    y.right.parent = y;
                }
                transplant(node, y);
                y.left = node.left;
                y.left.parent = y;
            }
            size--;
            node = find((T) o);
            if (node == null) return true;
        }
    }

    private void transplant(Node<T> u, Node<T> y) {
        if (u.parent == null) {
            root = y;
        } else if (u.equals(u.parent.left)) {
            u.parent.left = y;
        } else {
            u.parent.right = y;
        }
        if (y != null) {
            y.parent = u.parent;
        }

    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    public class BinaryTreeIterator implements Iterator<T> {

        private Node<T> current = null;

        private Node<T> predecessor(Node<T> x) {
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

        private BinaryTreeIterator() {
            // Добавьте сюда инициализацию, если она необходима
        }

        public Node<T> findNext(Node<T> x) {
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

        /**
         * Проверка наличия следующего элемента
         * Средняя
         */
        @Override
        public boolean hasNext() {
            return findNext(current) != null;
        }

        public boolean hasNextLast() {
            return predecessor(current) != null;
        }

        /**
         * Поиск следующего элемента
         * Средняя
         */
        @Override
        public T next() {
            current = findNext(current);
            return current.value;
        }


        public Node<T> nextNode() {
            current = findNext(current);
            return current;
        }

        public Node<T> nextNodeLast() {
            current = predecessor(current);
            return current;
        }


        /**
         * Удаление следующего элемента
         * Сложная
         */
        @Override
        public void remove() {
            Node<T> node = predecessor(current);
            if (node.left == null) {
                transplant(node, node.right);
            } else if (node.right == null) {
                transplant(node, node.left);
            } else {
                Node<T> y = node.right.minimum();
                if (y.parent != null) {
                    transplant(y, y.right);
                    y.right = node.right;
                    y.right.parent = y;
                }
                transplant(node, y);
                y.left = node.left;
                y.left.parent = y;
            }
        }
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

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        SortedSet<T> set = new TreeSet<>();
        BinaryTreeIterator it = (BinaryTreeIterator) iterator();
        Node<T> node = find(fromElement);
        while (node.value != fromElement) {
            set.add(node.value);
            node = it.findNext(node);
        }
        return set;
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        SortedSet<T> set = new TreeSet<>();
        BinaryTreeIterator it = (BinaryTreeIterator) iterator();
        Node<T> node = it.nextNode();
        while (node.value != toElement) {
            set.add(node.value);
            node = it.nextNode();
        }
        return set;
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        SortedSet<T> set = new TreeSet<>();
        BinaryTreeIterator it = (BinaryTreeIterator) iterator();
        Node<T> node = it.nextNodeLast();
        while (node.value.compareTo(fromElement) <= 0) {
            set.add(node.value);
            node = it.nextNodeLast();
        }
        return set;
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

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
