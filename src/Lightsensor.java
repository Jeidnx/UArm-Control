public class Lightsensor {

    private final int rows;
    private final int columns;
    private final boolean[][] grid;

    public Lightsensor(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new boolean[rows][columns];
    }

    public void updateGrid(String updateString){
        int counter = 0;
        String[] pinGrid = updateString.split(" ");

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                grid[i][j] = pinGrid[counter].equals("1");
                counter++;
            }
        }
    }

    public String toString(){
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                out.append(grid[i][j] ? "[x]\t" : "[ ]\t");
            }
            out.append("\n");
        }
        return out.toString();
    }
}
