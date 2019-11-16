package lesson7.annotations

data class MutableInt(@Volatile var value: Int) {
    operator fun plus(o: Int): Int {
        return o + value
    }

    operator fun minus(o: Int): Int {
        return o - value
    }

    fun addOne() {
        value++
    }

    operator fun unaryMinus(): Int {
        return -value
    }

    override fun toString(): String {
        return value.toString()
    }

    fun toDouble(): Double {
        return value.toDouble()
    }
}