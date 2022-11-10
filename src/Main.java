public class Main {
    public static void main(String[] args)  {
        UArm arm = new UArm("ttyACM0",2000);
        Board b = new Board(arm);
        Joystick j = new Joystick("ttyACM1", b);
        b.moveHome();
        while (true){}

    }

}