

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
    // Length of two. ROW and COLUMN Values
    private final int[] coords;

    public Board(UArm arm, int rows, int columns) {
       this.arm = arm;
       this.coords = new int[2];
       this.rows = rows;
       this.columns = columns;
       double lengthX = (MAXX - MINX);
       double lengthY = (MAXY - MINY);
       this.stepsizeX = lengthX / (rows -1 );
       this.stepsizeY = lengthY / (columns -1 );
    }

    public void move(int row, int column){
        if(row < 0 || row >= rows || column < 0 || column >= columns){
            throw new Error("Move outside of Board bounding.");
        }
        double xpos = (stepsizeX * (row)) + MINX;
        double ypos = (stepsizeY * (column)) + MINY;
        arm.moveHeight(TRAVELHEIGHT);
        this.coords[0] = row;
        this.coords[1] = column;
        arm.move(xpos, ypos, TRAVELHEIGHT);
    }

    public boolean moveRelative(int row, int column){
        int newRow = this.coords[0] + row;
        int newColumn = this.coords[1] + column;
        if(newRow < 0 || newRow >= rows || newColumn < 0 || newColumn >= columns){
            moveRelativeNotAllowed(row, column);
            return false;
        }
        this.coords[0] = newRow;
        this.coords[1] = newColumn;
        move(this.coords[0], this.coords[1]);
        return true;
    }
    public void moveHome(){
        this.coords[0] = 2;
        this.coords[1] = 2;
        this.move(2, 2);
    }
    public void moveAway(){
        this.arm.moveHeight(TRAVELHEIGHT);
        this.arm.move(80, 110, TRAVELHEIGHT);
    }

    public void moveRelativeNotAllowed(int row, int column){
        double xmove = row * stepsizeX * 0.25;
        double ymove = column * stepsizeY * 0.25;
        this.arm.moveRelative((int)xmove, (int)ymove, 0);
        this.arm.moveRelative(-(int)xmove, -(int)ymove, 0);

    }

    public boolean pickup(){
        if(pumpStatus){
            return false;
        }
        arm.moveHeight(BOARDHEIGHT);
        this.arm.setPumpStatus(true);
        this.pumpStatus = true;
        arm.moveHeight(TRAVELHEIGHT);
        return true;
    }
    public void pickupNotAllowed(){
        if(pumpStatus){
            return;
        }
        arm.moveHeight(TRAVELHEIGHT - ((TRAVELHEIGHT - BOARDHEIGHT) / (float)2));
        arm.moveHeight(TRAVELHEIGHT);
    }

    public boolean drop(){
        if(!pumpStatus){
            return false;
        }
        arm.moveHeight(BOARDHEIGHT);
        FutureHelper.waitMillis(500);
        this.arm.setPumpStatus(false);
        this.pumpStatus = false;
        arm.moveHeight(TRAVELHEIGHT);
        return true;
    }

    public boolean getPumpStatus(){
        return this.pumpStatus;
    }
    public int[] getCoords(){
        int[] newA = new int[2];
        newA[0] = this.coords[0];
        newA[1] = this.coords[1];
        return newA;
    }

}
