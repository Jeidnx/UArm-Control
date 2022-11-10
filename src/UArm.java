
import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class UArm {

    private final SerialPort port;
    private final BufferedReader reader;
    private final int moveSpeed;

    public UArm(String comport, int speed){
        this.moveSpeed = speed;

        this.port = SerialPort.getCommPort(comport);
        this.port.setBaudRate(115200);
        this.port.setNumDataBits(8);
        this.port.setNumStopBits(1);
        this.port.setParity(SerialPort.NO_PARITY);
        this.port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 100);
        if (!this.port.openPort()) {
            System.err.println("Verbindung zum UArm fehlgeschlagen.");
            System.exit(0);
        }

        this.reader = new BufferedReader(new InputStreamReader(this.port.getInputStream()));

        //init the arm
        try {
            FutureHelper.waitMillis(1000);

            if (!this.reader.ready()) {
                System.err.println("UArm hat seine Verbindungsnachricht nicht gesendet. Bitte überprüfe den Port.");
                System.exit(0);
            }

            while (this.reader.ready()) {
                System.out.println(this.reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This executor executes all UArm commands in sequence.
     */
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    public String move(double x, double y, double z) {
        return writeString("G0 X" + x + " Y" + y + " Z" + z + " F" + moveSpeed + "\n");
    }

    public String moveRelative(int x, int y, int z) {
        return writeString("G2204 X" + x + " Y" + y + " Z" + z + " F" + moveSpeed + "\n");
    }

    public String setPumpStatus(boolean status) {
        return writeString("M2231 V" + (status ? 1 : 0) + "\n");
    }

    public String height(double height){
        return writeString("G0 Z" + height + " F" + moveSpeed + "\n");
    }
    /**
     * @return the response sent by the UArm
     */
    private String writeString(String line) {
        System.out.println(line);

        return FutureHelper.get(CompletableFuture.supplyAsync(() -> {
            byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
            this.port.writeBytes(bytes, bytes.length);
            try {
                String s;

                while (this.port.bytesAvailable() <= 0) {
                    FutureHelper.waitMillis(50);
                }
                s = this.reader.readLine();
                return s;
            } catch (IOException e) {
                return "";
            }
        }, EXECUTOR_SERVICE));
    }
}
