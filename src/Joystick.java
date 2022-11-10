public class Joystick extends Thread{
    private Serial serial;
    private Board board;

    public Joystick(String comport, Board board){
        this.board = board;
        this.serial = new Serial(comport, 9600, 8, 1, Serial.PARITY_NONE);
        this.serial.open();
        this.start();
    }
    private void sleep(){
        return;
        /*try{
            Thread.sleep(2000);
        } catch (Exception ignored) {}*/
    }

    public void run() {
        System.out.println("Joystick thread started");
        while (true){
            if(this.serial.dataAvailable() > 0){
                String args = this.serial.readLine();
                if(args == null){
                    continue;
                }
                String[] args2 = args.split(" ");
                if(args2.length != 3){
                    continue;
                }
                if(args2[0].equals("1")){
                    board.dropOrPickup();
                }

                    int xpos = Integer.parseInt(args2[1]);
                    if(xpos > 700){
                        System.out.println("Right");
                        board.moveRelative(0, 1);
                        sleep();
                    }
                    if(xpos < 300){
                        System.out.println("Left");
                        board.moveRelative(0, -1);
                        sleep();
                    }

                    int ypos = Integer.parseInt(args2[2]);
                    if(ypos > 700){
                        System.out.println("Up");
                        board.moveRelative(-1, 0);
                        sleep();
                    }
                    if(ypos < 300){
                        System.out.println("Down");
                        board.moveRelative(1, 0);
                        sleep();
                    }
            }
        }

    }
}
