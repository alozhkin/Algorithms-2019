package lesson3

class Trie : AbstractMutableSet<String>(), MutableSet<String> {
    private val IS_WORD_CHAR = 0.toChar()

    private var modCount = 0

    override var size: Int = 0
        private set

    private class Node(val prev: Node?) {
        val children: MutableMap<Char, Node> = sortedMapOf()
    }

    private var root = Node(null)

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + IS_WORD_CHAR

    private fun findNextNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNextNode(element.withZero()) != null

    fun containsPrefix(element: String): Boolean =
        findNextNode(element) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node(current)
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
            modCount++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNextNode(element) ?: return false
        return removeNode(current)
    }

    private fun removeNode(node: Node): Boolean {
        if (node.children.remove(IS_WORD_CHAR) != null) {
            size--
            modCount++
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     * Сложная
     */
    override fun iterator(): MutableIterator<String> {
        return TrieIterator()
    }

    private inner class TrieIterator : MutableIterator<String> {
        private var expectedModCount = modCount
        private var currentString = ""
        private var nextStringBuilder = StringBuilder()
        private var currentNode: Node? = null
        private var visited = mutableSetOf<Node>()
        private var nextNode: Node? = findNextNode(root)

        /*
          Ресурсоёмкость: O(1)
          Трудоёмкость: O(1)
        */
        override fun hasNext(): Boolean {
            return nextNode != null
        }

        /*
          Ресурсоёмкость: в худшем случае рекурсивно ищу слово, идя от корня, не нахожу его и поднимаюсь к корню. Будет
          2 * длина слова вызовов функции, каждый из которых будет занимать место в стеке.
          O(длина слова * 2) = O(длина слова)
          Трудоёмкость: перебирает все вершины до следующего слова. В худшем случае, начнёт от корня, переберёт все
          вершины и не найдёт слово
          O(число вершин)
        */
        override fun next(): String {
            if (expectedModCount != modCount) {
                throw ConcurrentModificationException()
            }
            currentNode = nextNode
            currentString = nextStringBuilder.toString()
            nextNode = findNextNode(nextNode)
            return currentString
        }

        private fun findNextNode(node: Node?): Node? {
            if (node == null) {
                throw NoSuchElementException()
            } else {
                if (!visited.contains(node)) {
                    visited.add(node)
                    if (node.children.containsKey(IS_WORD_CHAR)) {
                        return node
                    }
                }
                for ((childCharacter, childNode) in node.children) {
                    if (childCharacter != IS_WORD_CHAR && !visited.contains(childNode)) {
                        nextStringBuilder.append(childCharacter)
                        return findNextNode(childNode)
                    }
                }
                if (node == root) return null
                nextStringBuilder.deleteCharAt(nextStringBuilder.length - 1)
                return findNextNode(node.prev)
            }
        }

        /*
          Ресурсоёмкость: O(1)
          Трудоёмкость: O(1)
        */
        override fun remove() {
            checkNotNull(currentNode)
            if (expectedModCount != modCount) {
                throw ConcurrentModificationException()
            }
            removeNode(currentNode!!)
            currentNode = null
            expectedModCount = modCount
        }
    }
}