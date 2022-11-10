

public class Board {

    private static int ROWS = 5;
    private static int COLUMNS = 5;

    // Board coordinates
    private static double MINX = 152;
    private static double MAXX = 311;

    private static double MINY = -80;
    private static double MAXY = 83;

    private static int TRAVELHEIGHT = 75;
    private static int BOARDHEIGHT = 58;

    private final UArm arm;
    private final double homeX;
    private final double homeY;
    private final double stepsizeX;
    private final double stepsizeY;

    private boolean pumpStatus = false;

    public Board(UArm arm) {
       this.arm = arm;
       double lengthX = (MAXX - MINX);
       double lengthY = (MAXY - MINY);

       this.homeX = lengthX / 2 + MINX;
       this.homeY = lengthY / 2 + MINY;
       this.stepsizeX = lengthX / (ROWS -1 );
       this.stepsizeY = lengthY / (COLUMNS -1 );
    }

    public String move(int row, int column){
        if(row < 1 || row > ROWS || column < 1 || column > COLUMNS){
            throw new Error("Move outside of Board bounding.");
        }
        double xpos = (stepsizeX * (row - 1)) + MINX;
        double ypos = (stepsizeY * (column - 1)) + MINY;
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
        arm.height(BOARDHEIGHT);
        if(pumpStatus){
            // Drop
            this.arm.setPumpStatus(false);
            this.pumpStatus = false;
        } else {
            // Pickup
            this.arm.setPumpStatus(true);
            this.pumpStatus = true;
        }
        return arm.height(TRAVELHEIGHT);
    }

}
