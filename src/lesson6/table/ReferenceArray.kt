package lesson6.table

class ReferenceArray<T : Comparable<T>>(val length: Int, private val defaultValue: T) {

    private val values = MutableList(length) { defaultValue }
    private val references = MutableList(length) { -1 }

    operator fun get(symbol: Int): T {
        return if (isInside(symbol)) {
            values[symbol]
        } else {
            defaultValue
        }
    }

    operator fun set(symbol: Int, value: T) {
        if (isInside(symbol)) {
            values[symbol] = value
        }
    }

    fun refer(symbol: Int, reference: Int) {
        if (isInside(symbol)) {
            references[symbol] = reference
        }
    }

    private fun isInside(symbol: Int): Boolean {
        return symbol in 0 until length
    }

    fun getRefToMax(): Int {
        return values.indices.maxBy { values[it] } ?: -1
    }

    fun getRef(currentIndex: Int): Int {
        return references[currentIndex]
    }
}