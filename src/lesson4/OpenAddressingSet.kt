package lesson4

class OpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    private companion object DeletedObject

    init {
        require(bits in 2..31)
    }

    private var modCount = 0

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null && current != DeletedObject) {
            if (current == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        modCount++
        return true
    }

    /**
     * Для этой задачи пока нет тестов, но вы можете попробовать привести решение и добавить к нему тесты
     */
    /*
      Ресурсоёмкость: O(1)
      Трудоёмкость: в худшем случае вся таблица с открытой адресацией забита, а удаляемого объекта нет
      O(capacity)
     */
    override fun remove(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                removeAt(index)
            }
            index = (index + 1) % capacity
            if (index == startingIndex) {
                return false
            }
            current = storage[index]
        }
        return false
    }

    private fun removeAt(index: Int) {
        storage[index] = DeletedObject
        size--
        modCount++
    }

    /**
     * Для этой задачи пока нет тестов, но вы можете попробовать привести решение и добавить к нему тесты
     */

    override fun iterator(): MutableIterator<T> {
        return OpenAddressingSetIterator()
    }

    private inner class OpenAddressingSetIterator : MutableIterator<T> {
        private var expectedModCount = modCount
        private var nextIndex = -1
        private var nowIndex = -1
        private var lastTimeRemoveWasCalled = false
        private var next: T? = findNext()

        /*
          Ресурсоёмкость: O(1)
          Трудоёмкость: O(1)
        */
        override fun hasNext(): Boolean {
            return next != null
        }

        /*
          Ресурсоёмкость: O(1)
          Трудоёмкость: в худшем случае переберу всю таблицу O(capacity)
        */
        override fun next(): T {
            if (expectedModCount != modCount) throw ConcurrentModificationException()
            if (next == null) throw NoSuchElementException()
            val now = next
            nowIndex = nextIndex
            next = findNext()
            lastTimeRemoveWasCalled = false
            return now!!
        }

        private fun findNext(): T? {
            if (size == 0) {
                return null
            } else {
                var current: Any?
                do {
                    nextIndex++
                    if (nextIndex == capacity) {
                        return null
                    }
                    current = storage[nextIndex]
                } while (current == null || current == DeletedObject)
                return current as T?
            }
        }

        /*
          Ресурсоёмкость: O(1)
          Трудоёмкость: O(1)
        */
        override fun remove() {
            if (expectedModCount != modCount) throw ConcurrentModificationException()
            check(!lastTimeRemoveWasCalled)
            removeAt(nowIndex)
            expectedModCount = modCount
            lastTimeRemoveWasCalled = true
        }
    }
}