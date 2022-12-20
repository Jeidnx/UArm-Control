import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        System.out.println("*******************");
        System.out.println("**               **");
        System.out.println("** UArm-Control  **");
        System.out.println("**               **");
        System.out.println("** Fünfer Mühle  **");
        System.out.println("**               **");
        System.out.println("*******************");
        System.out.println();
        System.out.println();

        int size = 5;

        SerialPortDescriptor[] portNames = {
                new SerialPortDescriptor("UArm", 115200),
                new SerialPortDescriptor("Spielfeld", 9600),
                new SerialPortDescriptor("Joystick", 9600),
        };
        Scanner sc = new Scanner(System.in);
        SerialPort[] ports = new SerialPort[portNames.length];

        for(int i = 0; i < portNames.length; i++){
            SerialPort[] tmp = SerialPort.getCommPorts();
            System.out.println("> Bitte schließe folgende Hardware an: " + portNames[i].name() + ". (bestätigen mit enter)");
            sc.nextLine();
            SerialPort[] ntmp = SerialPort.getCommPorts();
            List<SerialPort> newPorts = getNewPorts(tmp, ntmp);
            switch (newPorts.size()) {
                case 0 -> {
                    System.out.println("> Konnte kein neues serial Gerät erkennen. Bitte erneut versuchen.");
                    i--;
                }
                case 1 -> {
                    SerialPort device = connectDevice(portNames[i], newPorts.get(0));
                    if (device != null) {
                        ports[i] = device;
                        System.out.println("> Erfolgreich mit '" + portNames[i].name() + "' verbunden.");
                        System.out.println();
                    } else {
                        System.out.println("> Fehler beim verbinden. Bitte erneut versuchen.");
                        i--;
                    }
                }
                default -> {
                    System.out.println("> Es wurden mehrere serial Geräte gefunden. Bitte wähle eins aus der Liste: ");
                    int portsSize = newPorts.size();
                    for (int j = 0; j < portsSize; j++) {
                        SerialPort newPort = newPorts.get(j);
                        System.out.println("> " + (j  + 1) + ") Port Name: " + newPort.getSystemPortName());
                        System.out.println(">    Port Beschreibung: " + newPort.getPortDescription());
                        System.out.println(">    Port Pfad: " + newPort.getPortLocation());
                        System.out.println();
                    }
                    boolean connected = false;
                    while (!connected) {
                        int selected = sc.nextInt();
                        while (selected > portsSize) {
                            System.out.println("> '" + selected + "' ist größer als die erlaubte Eingabe. Bitte geben Sie höchstens '" + portsSize + "' ein:");
                            selected = sc.nextInt();
                        }
                        SerialPort device = connectDevice(portNames[i], newPorts.get(selected - 1));
                        if (device != null) {
                            ports[i] = device;
                            connected = true;
                            System.out.println("> Erfolgreich mit '" + portNames[i].name() + "' verbunden.");
                            System.out.println();
                        } else {
                            System.out.println("> Fehler beim verbinden. Bitte erneut versuchen.");
                            i--;
                        }
                    }
                }
            }
        }


        UArm arm = new UArm(ports[0], 500000);
        Board b = new Board(arm,size, size);
        Lightsensor ls = new Lightsensor(ports[1], size, size);
        Joystick j = new Joystick(ports[2], b);
        Muehle m = new Muehle(size);
        MuehleController mc = new MuehleController(b, ls, m, j);

        boolean playing = true;
        while(playing){
            b.moveAway();
            System.out.println("> Bitte entferne alle Gegenstände vom Spielbrett und stelle sicher das ausreichend Licht zur vefügung steht. (bestätigen mit enter)");
            sc.nextLine();
            mc.start();
            System.out.println("> Nochmal spielen? (j): ");
            String anw = sc.nextLine();
            if(!anw.equals("j")){
                playing = false;
            }
        }
        System.exit(0);

    }
    public static List<SerialPort> getNewPorts(SerialPort[] array1, SerialPort[] array2) {
        List<SerialPort> result = new ArrayList<>();
        for (SerialPort port : array2) {
            boolean found = false;
            for (SerialPort port2 : array1) {
                if (port.getSystemPortName().equals(port2.getSystemPortName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(port);
            }
        }
        return result;
    }
    public static SerialPort connectDevice(SerialPortDescriptor descriptor, SerialPort port){
        port.setBaudRate(descriptor.baudrate());
        port.setNumDataBits(8);
        port.setNumStopBits(1);
        port.setParity(SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 100);
        if (!port.openPort()) {
            return null;
        }
        //TODO: more exhaustive testing
        return port;
    }
}