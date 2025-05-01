package model;

public enum Direction {
    LEFT(0, -1), // 行变化0，列减1
    UP(-1, 0),// 行减1，列不变
    RIGHT(0, 1),// 行不变，列加1
    DOWN(1, 0);// 行加1，列不变

    private final int row;
    private final int col;

    Direction(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}