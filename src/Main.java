import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Muehle m = new Muehle(5);

        int x = 0;
        int y = 0;
        while(m.getState() == Muehle.State.STARTEN){
            if(x == 5){
                x = 0;
                y++;
            }
            System.out.println("Placing: " + x + " " + y);
            m.platziereStein(x, y);
            x++;
        }
        System.out.println(m.toString());
        Scanner scanner = new Scanner(System.in);
        while(m.isPlaying()){
            System.out.println(m.getAnweisung());
            System.out.println("Enter in the follwing order: xo yo xn yn");
            String anw = scanner.nextLine();
            String[] te = anw.split(" ");
            System.out.println(
                    m.bewegeStein(Integer.parseInt(te[0]), Integer.parseInt(te[1]), Integer.parseInt(te[2]), Integer.parseInt(te[3])) ?
                            "" : "Ungültiger Zug."
            );
            System.out.println(m.toString());
        }

        /*
        UArm arm = new UArm("ttyACM1", 1000);
        Board b = new Board(arm, "ttyACM0", 5 ,5);

        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 6; j++) {
                b.move(i, j);
                b.dropOrPickup();
                //move to next field
                if(j == 5){
                    b.move(i + 1, 1);
                }else{
                    b.move(i, j + 1);
                }
                b.dropOrPickup();
                System.out.println(b.getSensorGridString());
            }

        }*/

    }

}