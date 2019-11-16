package lesson6.table

data class Entry<T>(override val cell: Cell, override val value: T) : TableEntry<T>
