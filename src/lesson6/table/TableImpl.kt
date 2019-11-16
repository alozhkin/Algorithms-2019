package lesson6.table

import java.util.*

open class TableImpl<T>
internal constructor(override val length: Int, override val height: Int, private val defaultValue: T) : Table<T> {

    private val entryCache = mutableMapOf<Entry<T>, Entry<T>>()

    private val ar: MutableList<MutableList<T>> = MutableList(height) { MutableList(length) { defaultValue } }

    override operator fun get(cell: Cell): T {
        return if (isInside(cell)) {
            ar[cell.line][cell.symbol]
        } else {
            defaultValue
        }
    }

    override operator fun set(cell: Cell, value: T) {
        if (isInside(cell)) {
            ar[cell.line][cell.symbol] = value
        }
    }

    override fun isInside(cell: Cell): Boolean {
        val line = cell.line
        val symbol = cell.symbol
        return line in 0 until height && symbol in 0 until length
    }

    // если не влезает в массив, то возвращается defaultValue.
    override fun getNeighbourValue(cell: Cell, direction: Direction): T {
        val neighbour = direction.getNext(cell)
        return get(neighbour)
    }

    override fun shiftUp() {
        for (i in 0 until height - 1) {
            ar[i] = ar[i + 1]
        }
        ar[height - 1] = MutableList(length) { defaultValue }
    }

    override operator fun iterator(): Iterator<Entry<T>> {
        return TableIterator()
    }

    internal inner class TableIterator : Iterator<Entry<T>> {
        private var line = 0
        private var symbol = 0

        override fun hasNext(): Boolean {
            return line < height
        }

        override fun next(): Entry<T> {
            if (!hasNext()) throw NoSuchElementException()
            val cell = Cell(line, symbol)
            val entry = Entry(cell, get(cell))
            entryCache.putIfAbsent(entry, entry)
            setLineAndSymbol()
            return entryCache[entry]!!
        }

        private fun setLineAndSymbol() {
            symbol++
            if (symbol == length) {
                symbol = 0
                line++
            }
        }
    }
}

