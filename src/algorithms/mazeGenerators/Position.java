package algorithms.mazeGenerators;

import java.io.Serializable;

public class Position implements Serializable {
    private int RowIndex;
    private int ColumnIndex;

    public Position(int rowIndex, int columnIndex) {
        this.RowIndex = rowIndex;
        this.ColumnIndex = columnIndex;
    }

    public int getRowIndex() {
        return this.RowIndex;
    }

    public int getColumnIndex() {
        return this.ColumnIndex;
    }

    public String toString() {
        return "{" + this.RowIndex + "," + this.ColumnIndex + "}";
    }
}
