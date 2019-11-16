package lesson6.table

interface Table<T> : Iterable<TableEntry<T>> {

    val length: Int

    val height: Int

    operator fun get(cell: Cell): T

    operator fun set(cell: Cell, value: T)

    fun isInside(cell: Cell): Boolean

    fun getNeighbourValue(cell: Cell, direction: Direction): T

    fun shiftUp()
}