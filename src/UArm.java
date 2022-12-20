
import com.fazecast.jSerialComm.SerialPort;

public class UArm extends SerialDevice{

    private final int moveSpeed;

    public UArm(SerialPort port, int speed){
        super(port);
        this.moveSpeed = speed;
    }

    public void move(double x, double y, double z) {
        writeString("G0 X" + x + " Y" + y + " Z" + z + " F" + moveSpeed + "\n");
    }

    public void moveRelative(int x, int y, int z) {
        writeString("G2204 X" + x + " Y" + y + " Z" + z + " F" + moveSpeed + "\n");
    }

    public void setPumpStatus(boolean status) {
        writeString("M2231 V" + (status ? 1 : 0) + "\n");
    }

    public void moveHeight(double height){
        writeString("G0 Z" + height + " F" + moveSpeed + "\n");
    }
}
