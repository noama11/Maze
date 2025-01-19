package algorithms.mazeGenerators;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class Maze implements Serializable {
    private int[][] maze;
    private int rows;
    private int columns;
    private Position start;
    private Position end;

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public Maze(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.maze = new int[rows][columns];
        this.start = new Position(0, 0);
        this.end = new Position(rows - 1, columns - 1);
    }

    public void allWalls() {
        for(int i = 0; i < this.rows; ++i) {
            for(int j = 0; j < this.columns; ++j) {
                this.maze[i][j] = 1;
            }
        }

    }

    public void setCell(int row, int column, int value) {
        this.maze[row][column] = value;
    }

    public int getCell(int row, int column) {
        return this.maze[row][column];
    }

    public Position getStartPosition() {
        return this == null ? null : this.start;
    }

    public Position getGoalPosition() {
        return this == null ? null : this.end;
    }

    public void setGoal() {
        Random ran = new Random();
        int i;
        if (this.getColumns() % 2 == 0) {
            for(i = 0; i < this.getRows(); ++i) {
                this.setCell(i, this.getColumns() - 1, ran.nextInt(2));
            }

            this.setCell(this.getRows() - 2, this.getColumns() - 1, 0);
        }

        if (this.getRows() % 2 == 0) {
            for(i = 0; i < this.getColumns(); ++i) {
                this.setCell(this.getRows() - 1, i, ran.nextInt(2));
            }

            this.setCell(this.getRows() - 1, this.getColumns() - 2, 0);
        }

        this.setCell(this.getRows() - 1, this.getColumns() - 1, 0);
    }

    public void print() {
        for(int i = 0; i < this.rows; ++i) {
            System.out.print("[ ");

            for(int j = 0; j < this.columns; ++j) {
                System.out.print(this.maze[i][j] + " ");
            }

            System.out.println("]");
        }

    }

    public String toString() {
        int[][] tmp = this.maze;
        String S = (String)Arrays.stream(tmp).flatMapToInt(Arrays::stream).mapToObj(String::valueOf).collect(Collectors.joining(""));
        S = "\"" + S + "\"";
        return S;
    }

    public Maze(byte[] myByteMaze) {
        this.rows = Byte.toUnsignedInt(myByteMaze[0]) * 256 + Byte.toUnsignedInt(myByteMaze[1]);
        this.columns = Byte.toUnsignedInt(myByteMaze[2]) * 256 + Byte.toUnsignedInt(myByteMaze[3]);
        this.start = new Position(Byte.toUnsignedInt(myByteMaze[4]) * 256 + Byte.toUnsignedInt(myByteMaze[5]), Byte.toUnsignedInt(myByteMaze[6]) * 256 + Byte.toUnsignedInt(myByteMaze[7]));
        this.end = new Position(Byte.toUnsignedInt(myByteMaze[8]) * 256 + Byte.toUnsignedInt(myByteMaze[9]), Byte.toUnsignedInt(myByteMaze[10]) * 256 + Byte.toUnsignedInt(myByteMaze[11]));
        this.maze = new int[this.rows][this.columns];
        int index = 12;

        for(int row = 0; row < this.rows; ++row) {
            for(int column = 0; column < this.columns; ++column) {
                this.maze[row][column] = myByteMaze[index];
                ++index;
            }
        }

    }

    public byte[] toByteArray() {
        byte[] bytesArray = new byte[this.rows * this.columns + 12];
        bytesArray[0] = (byte)(this.rows / 256);
        bytesArray[1] = (byte)(this.rows % 256);
        bytesArray[2] = (byte)(this.columns / 256);
        bytesArray[3] = (byte)(this.columns % 256);
        bytesArray[4] = (byte)(this.getStartPosition().getRowIndex() / 256);
        bytesArray[5] = (byte)(this.getStartPosition().getRowIndex() % 256);
        bytesArray[6] = (byte)(this.getStartPosition().getColumnIndex() / 256);
        bytesArray[7] = (byte)(this.getStartPosition().getColumnIndex() % 256);
        bytesArray[8] = (byte)(this.getGoalPosition().getRowIndex() / 256);
        bytesArray[9] = (byte)(this.getGoalPosition().getRowIndex() % 256);
        bytesArray[10] = (byte)(this.getGoalPosition().getColumnIndex() / 256);
        bytesArray[11] = (byte)(this.getGoalPosition().getColumnIndex() % 256);
        int index = 12;

        for(int row = 0; row < this.rows; ++row) {
            for(int column = 0; column < this.columns; ++column) {
                bytesArray[index] = (byte)this.maze[row][column];
                ++index;
            }
        }

        return bytesArray;
    }
}
