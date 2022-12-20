import com.fazecast.jSerialComm.SerialPort;

public class Lightsensor extends SerialDevice {

    private final int rows;
    private final int columns;
    private final boolean[][] grid;

    public Lightsensor(SerialPort serial, int rows, int columns) {
        super(serial);
        this.rows = rows;
        this.columns = columns;
        this.grid = new boolean[rows][columns];
    }
    private void updateGrid(){
        this.writeString("g");
        while(!this.isDataAvailable()){}
        this.updateGrid(this.readString());
    }

    private void updateGrid(String updateString){
        int counter = 0;
        String[] pinGrid = updateString.split(" ");

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(pinGrid[counter] == null){
                    throw new Error("Fehler beim parsen der Lichtsensordaten.");
                }
                grid[i][j] = pinGrid[counter].equals("1");
                counter++;
            }
        }
    }
    public int[] getFirstPlacedTile(boolean[][] old){
        this.updateGrid();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(this.grid[i][j] != old[i][j]){
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
    public boolean[][] getGrid() {
        this.updateGrid();
        boolean[][] newA = new boolean[rows][columns];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                newA[i][j] = this.grid[i][j];
            }
        }

        return newA;
    }

    public String toString(){
        this.updateGrid();
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
