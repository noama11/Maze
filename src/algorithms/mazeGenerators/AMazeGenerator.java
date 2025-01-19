package algorithms.mazeGenerators;

public abstract class AMazeGenerator implements IMazeGenerator {
    public AMazeGenerator() {
    }

    public abstract Maze generate(int var1, int var2);

    public long measureAlgorithmTimeMillis(int rows, int columns) {
        if (rows > 0 && columns > 0) {
            long before = System.currentTimeMillis();
            this.generate(rows, columns);
            long after = System.currentTimeMillis();
            return after - before;
        } else {
            return 1L;
        }
    }
}
