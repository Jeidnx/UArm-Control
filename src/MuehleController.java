import java.util.Scanner;

public class MuehleController {
    private final Muehle spiel;
    private final Board board;
    private final Lightsensor ls;
    private final Joystick joystick;

    public MuehleController(Board b,Lightsensor l, Muehle m, Joystick j){
        this.spiel = m;
        this.board = b;
        this.joystick = j;
        this.ls = l;
    }

    public void start(){
        spiel.starteSpiel();

        while(spiel.getState() == Muehle.State.STARTEN){
            System.out.println(spiel.getAnweisung());
                boolean[][] before = ls.getGrid();
                Scanner sc = new Scanner(System.in);
                int[] tile = null;
                while(tile == null){
                    System.out.println("Platziere einen Stein und drücke ENTER.");
                    sc.nextLine();
                    tile = this.ls.getFirstPlacedTile(before);
                    if(tile == null){
                        System.out.println("Das hat leider nicht geklappt. Versuche es erneut.");
                    }
                }
                if(!this.spiel.platziereStein(tile[0], tile[1])){
                    throw new Error("Spiel ist nicht richtig Synchronisiert.");
                }
            System.out.println(this.spiel);
        }
        this.board.moveHome();

        System.out.println(spiel);
        while(spiel.isPlaying()){
            System.out.println(spiel.getAnweisung());
            Muehle.Spieler amZug = this.spiel.getAmZug();
            int[][] allowedCoords = this.spiel.getAllowedCoords(amZug);
            int[] starting = this.joystick.movePiece(allowedCoords);
            int[] ending = this.board.getCoords();
            if(!this.spiel.bewegeStein(starting[0], starting[1], ending[0], ending[1])){
                throw new Error("Spiel ist nicht richtig Synchronisiert.");
            }
            System.out.println(spiel);
        }
    }

}
