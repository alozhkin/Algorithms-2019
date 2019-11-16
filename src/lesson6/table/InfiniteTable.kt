package lesson6.table

class InfiniteTable<T>(length: Int, defaultValue: T) : TableImpl<T>(length, 2, defaultValue) {
    private val entryCache = mutableMapOf<Entry<T>, Entry<T>>()

    override fun iterator(): Iterator<Entry<T>> {
        return InfinityTableIterator()
    }

    inner class InfinityTableIterator : Iterator<Entry<T>> {
        private var line = 1
        private var symbol = -1

        override fun hasNext() = true

        override fun next(): Entry<T> {
            setLineAndSymbol()
            val cell = Cell(line, symbol)
            val entry = Entry(cell, get(cell))
            entryCache.putIfAbsent(entry, entry)
            return entryCache[entry]!!
        }

        private fun setLineAndSymbol() {
            if (symbol == length - 1) {
                symbol = -1
                shiftUp()
            }
            symbol++
        }

    }
}