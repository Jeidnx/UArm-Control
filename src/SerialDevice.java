import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SerialDevice {
    private SerialPort port;

    private final BufferedReader reader;
    public SerialDevice(SerialPort port){
        if(port == null){
            throw new Error("SerialDevice sollte keine null-objekte erhalten.");
        }
        this.port = port;
        this.reader = new BufferedReader(new InputStreamReader(this.port.getInputStream()));
    }

    protected void writeString(String line) {
        byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
        this.port.writeBytes(bytes, bytes.length);
    }
    protected String readString(){
        try {
            if(this.port.bytesAvailable() > 0) {
                return this.reader.readLine();
            }
        } catch (Exception ignored) {}
        return "";
    }
    protected boolean isDataAvailable(){
        return this.port.bytesAvailable() > 0;
    }
    protected String readAll(){
        StringBuilder sb = new StringBuilder();
        try {
            while(this.port.bytesAvailable() > 0) {
                sb.append(this.reader.readLine());
            }
        } catch (Exception ignored) {}
        return sb.toString();
    }
}
