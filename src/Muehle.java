import java.util.LinkedList;

public class Muehle {
    private final int size;

    public enum Spieler {
        KEINER, BLAU, GOLD
    }
    public enum State {
        ANGEHALTEN, STARTEN, LAEUFT
    }
    private State state;

    private final Spieler[][] grid;
    private Spieler amZug;
    private Spieler gewinner;
    private int placeCounter;

    public Muehle(int size){
        this.placeCounter = 0;
        this.state = State.ANGEHALTEN;
        this.size = size;
        this.grid = new Spieler[size][size];
        this.amZug = Spieler.KEINER;
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                this.grid[x][y] = Spieler.KEINER;
            }
        }
        this.starteSpiel();
    }
    public boolean platziereStein(int x, int y){
        if (this.state != State.STARTEN){
            return false;
        }
        if (this.grid[x][y] != Spieler.KEINER){
            return false;
        }
        this.grid[x][y] = this.amZug;
        this.wechselSpieler();
        this.placeCounter++;
        if(this.placeCounter == 10){
            this.state = State.LAEUFT;
        }
        this.pruefeGewinner();
        return true;
    }

    public boolean bewegeStein(int xOrig, int yOrig, int xNeu, int yNeu){
        if(this.state != State.LAEUFT){
            return false;
        }
        if(this.grid[xOrig][yOrig] != this.amZug){
            return false;
        }
        if(this.grid[xNeu][yNeu] != Spieler.KEINER){
            return false;
        }
        boolean xValid = (xOrig == (xNeu + 1)) || (xOrig == (xNeu -1));
        boolean yValid = (yOrig == (yNeu + 1)) || (yOrig == (yNeu -1));
        if(xValid^yValid){
            this.grid[xOrig][yOrig] = Spieler.KEINER;
            this.grid[xNeu][yNeu] = this.amZug;
            this.pruefeGewinner();
            this.wechselSpieler();
            return true;
        }
        return false;
    }
    private void wechselSpieler(){
        if(this.amZug == Spieler.KEINER){
            return;
        }
        this.amZug = this.amZug == Spieler.BLAU ? Spieler.GOLD : Spieler.BLAU;
    }

    private void pruefeGewinner(){
        if(pruefeGewinner(Spieler.BLAU)){
            this.gewinner = Spieler.BLAU;
            this.amZug = Spieler.KEINER;
            this.state = State.ANGEHALTEN;
            return;
        }
        if(pruefeGewinner(Spieler.GOLD)){
            this.gewinner = Spieler.GOLD;
            this.amZug = Spieler.KEINER;
            this.state = State.ANGEHALTEN;
        }
    }
    public boolean pruefeGewinner(Spieler player){

        for (int j = 0; j<this.size-4 ; j++ ){
            for (int i = 0; i<this.size; i++){
                if (this.grid[i][j] == player && this.grid[i][j+1] == player && this.grid[i][j+2] == player && this.grid[i][j+3] == player && this.grid[i][j+4] == player){
                    return true;
                }
            }
        }
        for (int i = 0; i<this.size-4 ; i++ ){
            for (int j = 0; j<this.size; j++){
                if (this.grid[i][j] == player && this.grid[i+1][j] == player && this.grid[i+2][j] == player && this.grid[i+3][j] == player && this.grid[i+4][j] == player){
                    return true;
                }
            }
        }
        for (int i=4; i<this.size; i++){
            for (int j=0; j<this.size-4; j++){
                if (this.grid[i][j] == player && this.grid[i-1][j+1] == player && this.grid[i-2][j+2] == player && this.grid[i-3][j+3] == player && this.grid[i-4][j+4] == player)
                    return true;
            }
        }
        for (int i=4; i<this.size; i++){
            for (int j=4; j<this.size; j++){
                if (this.grid[i][j] == player && this.grid[i-1][j-1] == player && this.grid[i-2][j-2] == player && this.grid[i-3][j-3] == player && this.grid[i-4][j-4] == player)
                    return true;
            }
        }
        return false;
    }

    public String getAnweisung(){
        return switch (this.state){
            case ANGEHALTEN -> "Das Spiel muss erst gestartet werden, bevor ein Zug gemacht werden kann.";
            case STARTEN -> "Spieler '" + this.amZug + "' darf einen Stein auf dem Feld platzieren.";
            case LAEUFT -> "Spieler '" + this.amZug + "' darf einen Stein verschieben.";
        };
    }
    public void starteSpiel(){
        this.gewinner = Spieler.KEINER;
        this.placeCounter = 0;
        this.state = State.STARTEN;
        //TODO: Decide who gets to go first somehow
        this.amZug = Spieler.BLAU;
    }
    public boolean isPlaying(){
        return this.state != State.ANGEHALTEN;
    }
    public int[][] getAllowedCoords(Spieler sp){
        LinkedList<int[]> coords = new LinkedList<>();
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                if(this.grid[x][y] == sp){
                    coords.add(new int[]{x, y});
                }
            }
        }

        int matches = coords.size();
        int[][] re = new int[matches][2];

        for(int i = 0; i < matches; i++){
            re[i] = new int[] {coords.get(i)[0], coords.get(i)[1]};
        }

        return re;
    }
    public State getState(){
        return this.state;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("----------\n");
        sb.append("Mühle\n");
        sb.append("Spielstatus: ").append(this.state).append("\n");
        sb.append("Gewinner: ").append(this.gewinner).append("\n");
        sb.append("Am zug:\t").append(this.amZug).append("\n");
        sb.append("  \t");
        for(int i = 0; i < size; i++){
            sb.append("Y").append(i).append("\t");
        }
        sb.append("\n");
        for(int x = 0; x < size; x++){
            sb.append("X").append(x).append("\t");
            for(int y = 0; y < size; y++){
                //sb.append(x).append(y);
                sb.append(switch (this.grid[x][y]){
                    case BLAU -> "[B]\t";
                    case GOLD -> "[G]\t";
                    case KEINER -> "[ ]\t";
                });
            }
            sb.append("\n");
        }
        sb.append("----------");

        return sb.toString();
    }
    public Spieler getAmZug(){
        return this.amZug;
    }
}
