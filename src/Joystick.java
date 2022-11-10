public class Joystick extends Thread{
    private Serial serial;
    private Board board;

    public Joystick(String comport, Board board){
        this.board = board;
        this.serial = new Serial(comport, 9600, 8, 1, Serial.PARITY_NONE);
        this.serial.open();
        this.start();
    }
    private void flush(){
        this.serial.readBytes();
    }
    public void run() {
        System.out.println("Joystick thread started");
        while (true){
            if(this.serial.dataAvailable() > 0){
                String arg = this.serial.readLine();
                switch (arg) {
                    case "b" -> board.dropOrPickup();
                    case "u" -> board.moveRelative(-1, 0);
                    case "d" -> board.moveRelative(1, 0);
                    case "r" -> board.moveRelative(0, 1);
                    case "l" -> board.moveRelative(0, -1);
                }
                this.flush();
            }
        }

    }
}
