package lesson6.table;

public enum Direction {
    NONE(new Cell(0, 0)),
    DOWN(new Cell(1, 0)),
    BOTTOM_RIGHT(new Cell(1, 1)),
    RIGHT(new Cell(0, 1)),
    UPPER_RIGHT(new Cell(-1, 1)),
    UP(new Cell(-1, 0)),
    UPPER_LEFT(new Cell(-1, -1)),
    LEFT(new Cell(0, -1)),
    BOTTOM_LEFT(new Cell(1, -1));

    private Cell cell;

    Direction(Cell cell) {
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    public Cell getNext(Cell cell) {
        return cell.plus(this.cell);
    }

}
