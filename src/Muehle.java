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
        this.grid = new Spieler[5][5];
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
        System.out.println("xValid: " + xValid + " yValid: " + yValid);
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
        //TODO: implement diagonal win

        Spieler horizontalWinner = Spieler.KEINER;
        int counter = 0;
        Spieler sp = Spieler.KEINER;
        outer:for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                Spieler tsp = this.grid[x][y];
                if(tsp == Spieler.KEINER){
                    counter = 0;
                    continue;
                }
                if(tsp == sp){
                    counter++;
                    if(counter == 5){
                        horizontalWinner = sp;
                        break outer;
                    }
                }else{
                    sp = tsp;
                    counter = 1;
                }
            }
        }
        Spieler verticalWinner = Spieler.KEINER;
        outer:for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                Spieler tsp = this.grid[x][y];
                if(tsp == Spieler.KEINER){
                    counter = 0;
                    continue;
                }
                if(tsp == sp){
                    counter++;
                    if(counter == 5){
                        verticalWinner = sp;
                        break outer;
                    }
                }else{
                    sp = tsp;
                    counter = 1;
                }
            }
        }
        if(horizontalWinner != Spieler.KEINER){
            this.gewinner = horizontalWinner;
            this.state = State.ANGEHALTEN;
            this.amZug = Spieler.KEINER;
        }

        if(verticalWinner != Spieler.KEINER){
            this.gewinner = verticalWinner;
            this.state = State.ANGEHALTEN;
            this.amZug = Spieler.KEINER;
        }
    }
    public String getAnweisung(){
        return switch (this.state){
            case ANGEHALTEN -> "Das Spiel muss erst gestartet werden, bevor ein Zug gemacht werden kann.";
            case STARTEN -> "Spieler '" + this.amZug + "' darf einen Stein auf dem Feld platzieren.";
            case LAEUFT -> "Spieler '" + this.amZug + "' darf einen Stein verschieben.";
        };
    }
    private void starteSpiel(){
        this.gewinner = Spieler.KEINER;
        this.placeCounter = 0;
        this.state = State.STARTEN;
        //TODO: Decide who gets to go first somehow
        this.amZug = Spieler.BLAU;
    }
    public boolean isPlaying(){
        return this.state != State.ANGEHALTEN;
    }
    public Spieler getSpieler(int x, int y){
        if(x < 0 || x > size || y < 0 || y > size){
            throw new Error("getSpieler() darf nur Zahlen zwischen 0 und " + size + " erhalten.");
        }
        return this.grid[x][y];
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
            sb.append("X").append(i).append("\t");
        }
        sb.append("\n");
        for(int y = 0; y < size; y++){
            sb.append("Y").append(y).append("\t");
            for(int x = 0; x < size; x++){
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
}
