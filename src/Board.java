

public class Board {

    private final int rows;
    private final int columns;

    // Board coordinates
    private static double MINX = 152;
    private static double MAXX = 311;

    private static double MINY = -80;
    private static double MAXY = 83;

    private static int TRAVELHEIGHT = 75;
    private static int BOARDHEIGHT = 58;

    private final UArm arm;
    private final double stepsizeX;
    private final double stepsizeY;

    private boolean pumpStatus = false;

    public Board(UArm arm, int rows, int columns) {
       this.arm = arm;
       this.sensorSerial = new Serial(lightsensorPort, 9600, 8, 1, Serial.PARITY_NONE);
       if(!this.sensorSerial.open()){
           System.out.println("Konnte nicht mit Arduino auf Port: " + lightsensorPort + " verbinden.");
           System.exit(1);
       }
       this.sensorGrid = new Lightsensor(rows, columns);
       this.rows = rows;
       this.columns = columns;
       double lengthX = (MAXX - MINX);
       double lengthY = (MAXY - MINY);
       this.stepsizeX = lengthX / (rows -1 );
       this.stepsizeY = lengthY / (columns -1 );
    }

    public String move(int row, int column){
        if(row < 1 || row > rows || column < 1 || column > columns){
            throw new Error("Move outside of Board bounding.");
        }
        double xpos = (stepsizeX * (row - 1)) + MINX;
        double ypos = (stepsizeY * (column - 1)) + MINY;
        arm.moveHeight(TRAVELHEIGHT);
       return arm.move(xpos, ypos, TRAVELHEIGHT);
    }

    public String moveRelative(int row, int column){
        double xmove = row * stepsizeX;
        double ymove = column * stepsizeY;

        System.out.println("Moving to:");
        System.out.println(xmove);
        System.out.println(ymove);
        return arm.moveRelative((int)xmove, (int)ymove,0);
    }
    public void moveHome(){
        this.arm.move(homeX, homeY, TRAVELHEIGHT);
    }

    public String dropOrPickup(){
        arm.moveHeight(BOARDHEIGHT);
        if(pumpStatus){
            // Drop
            this.arm.setPumpStatus(false);
            this.pumpStatus = false;
        } else {
            // Pickup
            this.arm.setPumpStatus(true);
            this.pumpStatus = true;
        }
        return arm.moveHeight(TRAVELHEIGHT);
    }

}
