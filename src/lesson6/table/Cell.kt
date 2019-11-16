package lesson6.table

data class Cell(val line: Int, val symbol: Int) : Comparable<Cell> {
    override fun compareTo(other: Cell) = compareValuesBy(this, other, { it.line }, { it.symbol })

    operator fun plus(cell: Cell): Cell {
        return Cell(line + cell.line, symbol + cell.symbol)
    }
}