import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;

public class Joystick extends SerialDevice{
    private Board board;
    private enum Command {
        pump, up, down, right, left
    }

    public Joystick(SerialPort port, Board board){
        super(port);
        this.board = board;
    }

    // Returns the starting coordinates of the move
    public int[] movePiece(int[][] allowedCoords){
        int[] starting;
        while (!this.board.getPumpStatus()){
            Command c = readCommand();
            if(c == null){
                continue;
            }
            switch (c) {
                case pump -> {
                    boolean hasTriggered = false;
                    for(int[] coord: allowedCoords){
                        if(Arrays.equals(coord, this.board.getCoords())){
                            hasTriggered = true;
                            this.board.pickup();
                            starting = this.board.getCoords();
                            if(this.doSecondaryMove()){
                                return starting;
                            }
                        }
                    }
                    if(!hasTriggered){
                        this.board.pickupNotAllowed();
                    }
                }
                case up -> board.moveRelative(-1, 0);
                case down -> board.moveRelative(1, 0);
                case right -> board.moveRelative(0, 1);
                case left -> board.moveRelative(0, -1);
            }
        }
        return null;
    }
    private boolean doSecondaryMove(){
        Command curr = Command.pump;
        while (this.board.getPumpStatus()) {
            Command n = readCommand();
            if(n == null){
                continue;
            }
            switch (n) {
                case pump -> {
                    board.drop();
                    return curr != Command.pump;
                }
                case up -> {
                    if(curr == Command.pump){
                        if(board.moveRelative(-1, 0)){
                            curr = Command.up;
                        }
                        break;
                    }
                    if(curr == Command.down){
                        if(board.moveRelative(-1 , 0)){
                            curr = Command.pump;
                        }
                        break;
                    }
                    board.moveRelativeNotAllowed(-1, 0);
                }
                case down -> {
                    if(curr == Command.pump){
                        if(board.moveRelative(1, 0)){
                            curr = Command.down;
                        }
                        break;
                    }
                    if(curr == Command.up){
                        if(board.moveRelative(1, 0)){
                            curr = Command.pump;
                        }
                        break;
                    }
                    board.moveRelativeNotAllowed(1, 0);
                }
                case right -> {
                    if(curr == Command.pump){
                        if(board.moveRelative(0, 1)){
                            curr = Command.right;
                        }
                        break;
                    }
                    if(curr == Command.left){
                        if(board.moveRelative(0, 1)){
                            curr = Command.pump;
                        }
                        break;
                    }
                    board.moveRelativeNotAllowed(0, 1);
                }
                case left -> {
                    if(curr == Command.pump){
                        if(board.moveRelative(0, -1)){
                            curr = Command.left;
                        }
                        break;
                    }
                    if(curr == Command.right){
                        if(board.moveRelative(0, -1)){
                            curr = Command.pump;
                        }
                        break;
                    }
                    board.moveRelativeNotAllowed(0, -1);
                }
            }
        }
        return false;
    }
    private Command readCommand(){
        if(!this.isDataAvailable()){
            return null;
        }
        String arg = this.readString();
        this.readAll();
        return switch (arg) {
            case "b" -> Command.pump;
            case "u" -> Command.up;
            case "d" -> Command.down;
            case "r" -> Command.right;
            case "l" -> Command.left;
            default -> null;
        };
    }
}
