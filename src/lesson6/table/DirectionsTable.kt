package lesson6.table

class DirectionsTable<T>(table: Table<T>) : Table<T> by table {
    private val dir = MutableList(table.height) { MutableList(table.length) { Direction.NONE } }

    fun refer(cell: Cell, direction: Direction) {
        if (isInside(cell)) {
            set(cell, getNeighbourValue(cell, direction))
            dir[cell.line][cell.symbol] = direction
        }
    }

    fun set(cell: Cell, direction: Direction, value: T) {
        if (isInside(cell)) {
            set(cell, value)
            dir[cell.line][cell.symbol] = direction
        }
    }

    fun getDirection(cell: Cell): Direction {
        return if (isInside(cell)) {
            dir[cell.line][cell.symbol]
        } else {
            Direction.NONE
        }
    }
}