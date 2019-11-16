package lesson6.table

interface TableEntry<T> {
    val cell: Cell
    val value: T
    operator fun component1(): Cell {
        return cell
    }

    operator fun component2(): T {
        return value
    }
}