import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

/**
 *  <b>Klasse Serial: Serielle Ein-/Ausgabe</b> <br /> <br/>
 *
 *  <b>Windows 10 - Version 09/2020, ab JDK > Java 10</b> <br/>
 *
 *  Infotext (aus Serial - Version 3.0):<br/>
 *
 *  Ein Objekt der Klasse Serial kapselt eine serielle Schnittstelle (RS232) und 
 *  damit einen seriellen Port. Festlegung der Uebertragungs-Parameter kann nur 
 *  bei der Objekt-Erzeugung vorgenommen werden.<br/>
 *  <br/>
 *  Wenn alles gut gegangen ist, ist das Serial-Objekt danach bereit.
 *  Dies kann durch den Rueckgabewert der Methode
 *  {@link Serial#open open()} festgestellt werden. Diese liefert true bei 
 *  bereit).<br />
 *  <br>
 *  Das Serial-Objekt gestattet im Zustand &quot;bereit&quot; das Lesen bzw.
 *  Schreiben von einzelnen Zeichen, Bytes, Byte-Arrays oder Strings von der 
 *  bzw. auf die serielle Schnittstelle.<br />
 *  <br>
 *  <b>Installationshinweise</b><br/>
 *  <b>
 *  siehe IB_BG_PI_Installationshinweise_serielle_Schnittstelle.pdf<br>
 *
 *  Fuer diese Version ist folgendes Paket einzubinden:<br>
 *  @see <a href="jSerialComm.jar (Stand 31.08.2020 die Version 2.6.2)">https://fazecast.github.io/jSerialComm</a>
 *  <br>
 *  <a href="API:">https://fazecast.github.io/jSerialComm/javadoc/com/fazecast/jSerialComm/package-summary.html</a>
 *  <br>
 *  <b><font color=red>WICHTIG: Das Paket gnu.io (rxtx.jar) ist NICHT mehr notwendig.</font></b>
 *
 *
 *  @author  Rainer Wieland, Juergen Mang
 *  @version 5.0.8 vom 07.09.2021
 */
public class Serial
{

    // Anfang Attribute
    private static final boolean DEBUG_SERIAL = false; // Initiale Belegung (Testausgaben)
    // auf true setzen, wenn Testausgaben
    // eingeschaltet werden sollen

    public static final String VERSION = "5.0.6 vom 14.09.2020";

    public static final int DATABITS_5   = 5;
    public static final int DATABITS_6   = 6;
    public static final int DATABITS_7   = 7;
    public static final int DATABITS_8   = 8;

    public static final int STOPBITS_1   = SerialPort.ONE_STOP_BIT;
    public static final int STOPBITS_1_5 = SerialPort.ONE_POINT_FIVE_STOP_BITS;
    public static final int STOPBITS_2   = SerialPort.TWO_STOP_BITS;

    public static final int PARITY_NONE  = SerialPort.NO_PARITY;
    public static final int PARITY_EVEN  = SerialPort.EVEN_PARITY;
    public static final int PARITY_ODD   = SerialPort.ODD_PARITY;

    private BufferedReader br   = null;
    private DataInputStream dis = null;
    private InputStream is      = null;
    private OutputStream os     = null;

    private SerialPort serialPort = null;

    private boolean isOpen;

    // Attribute zur Kurzbeschreibung:
    private String portName;
    private int baudrate;
    private int dataBits;
    private int stopBits;
    private int parity;
    // Ende Attribute

    // KONSTRUKTOR
    /**
     * Konstruktor der Klasse Serial. Es werden alle erforderlichen Attribute 
     * dieser Klasse initialisiert.
     * 
     * Es werden automatisch die entsprechenden Streams zum Lesen und Schreiben 
     * testweise geoeffnet und wieder geschlossen.
     * Wird ein Fehler erkannt (zum Beispiel durch einen falscher Parameterwert), wird das Programm mit entsprechendem Exit-code beendet
     * -1: Ungültiger Portname
     * -2: Ungültige Baudrate
     * -3: Ungültige Databists
     * -4: Ungültige Stopbits
     * -5: Ungülitge Parität
     * -6: Weiterer, nicht genauer spezifizierter Fehler 
     * 
     * @param portName Der Port der geoeffnet werden soll. Zum Beispiel COM11.
     * @param baudrate Die Übertragungsrate die verwendet werden soll. Beispiel: 9600
     * @param dataBits Wie viele Bits uebertragen werden sollen (DATABITS_5...DATABITS_8). Standard: DATABITS_8.
     * @param stopBits Wie viele Stop Bits gesendet werden sollen (STOPBITS_1, STOPBITS_1_5, STOPBITS_2)
     * @param parity Welche Art von Paritaetsbits verwendet werden (PARITY_NONE, PARITY_EVEN, PARITY_ODD).
     */
    public Serial(String portName, int baudrate, int dataBits, int stopBits, int parity)// throws Exception
    {
        this.baudrate = -1;
        this.portName = "";
        this.dataBits = -1;
        this.stopBits = -1;
        this.parity   = -1;

        if(DEBUG_SERIAL)
            System.out.println("Serial - Version " + VERSION + " used jSerialComm-Version " + com.fazecast.jSerialComm.SerialPort.getVersion());

        if(!isPortNameValid(portName))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in portName [" + portName +"]");
            System.exit(-1); //throw new Exception("PortName not valid!");
        }

        if(!isBaudRateValid(baudrate))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in baudrate [" + baudrate +"]");
            System.exit(-2); //throw new Exception("BaudRate not valid!");
        }

        if(!isDataBitsValid(dataBits))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in dataBits [" + dataBits +"]");
            System.exit(-3); //throw new Exception("Databits not valid!");
        }

        if(!isStopBitsValid(stopBits))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in stopBits [" + stopBits +"]");
            System.exit(-4); //throw new Exception("Sopbits not valid!");
        }

        if(!isParityValid(parity))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in parity [" + parity +"]");
            System.exit(-5); //throw new Exception("Parity not valid!");
        }

        try
        {
            serialPort = SerialPort.getCommPort(portName);
            setPortName(portName);
            setBaudrate(baudrate);
            setDataBits(dataBits);
            setParity(parity);
            setStopBits(stopBits);

            if(open())
                close();
            else
            {
                if(DEBUG_SERIAL)
                    System.out.println("Achtung! Eine Instanz der Klasse Serial wurde nicht erstellt, beim Instanziieren der Klasse ist etwas schief gegangen");
                System.exit(-6);
            }
        }
        catch (SerialPortInvalidPortException spipe)
        {
            if(DEBUG_SERIAL)
                System.out.println("Achtung! Eine Instanz der Klasse Serial wurde nicht erstellt, beim Instanziieren der Klasse ist folgender Fehler aufgetreten:\n" + spipe.toString());
            System.exit(-7);
        }
    }

    /**
     * ***************************************************************************
     * METHODEN HESSISCHES LANDESABITUR
     * ***************************************************************************
     */  

    /**
     * oeffnet die serielle Scnittstelle. Liefert true, wenn die Schnittstelle 
     * verwendbar / geoeffnet werden kann, sonst false.
     * 
     * @return true, wenn die Schnittstelle geoeffnet werden konnte, sonst false.
     */
    public boolean open()
    {
        if(DEBUG_SERIAL)
            System.out.println("Die Methode 'public boolean open()' wurde aufgerufen");

        if(serialPort != null && !serialPort.isOpen() && serialPort.openPort())
        {
            if(serialPort.setBaudRate(this.baudrate))
            {
                if(DEBUG_SERIAL)
                    System.out.println("Baudrate wurde auf " + this.baudrate +  " gesetzt!");
            }
            else
            {
                if(DEBUG_SERIAL)
                    System.out.println("Baudrate konnte nicht auf " + this.baudrate +  " gesetzt werden!");
                return false;
            }

            if(serialPort.setParity(this.parity))
            {
                if(DEBUG_SERIAL)
                    System.out.println("Parity wurde auf " + this.parity +  " gesetzt!");
            }
            else
            {
                if(DEBUG_SERIAL)
                    System.out.println("Parity konnte nicht auf " + this.parity +  " gesetzt werden!");
                return false;
            }

            if(serialPort.setNumDataBits(this.dataBits))
            {
                if(DEBUG_SERIAL)
                    System.out.println("Databits wurde auf " + this.dataBits +  " gesetzt!");
            }
            else
            {
                if(DEBUG_SERIAL)
                    System.out.println("Databits konnte nicht auf " + this.dataBits +  " gesetzt werden!");
                return false;
            }

            if(serialPort.setNumStopBits(this.stopBits))
            {
                if(DEBUG_SERIAL)
                    System.out.println("Stopbits wurde auf " + this.stopBits +  " gesetzt!");
            }
            else
            {
                if(DEBUG_SERIAL)
                    System.out.println("Stopbits konnte nicht auf " + this.stopBits +  " gesetzt werden!");
                return false;
            }

            this.serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
            if(DEBUG_SERIAL)
                System.out.println("Flowcontrol (Hardwarehandshake) wurde abgeschaltet");

            // TimeOut-Konfigurationen:
            this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            if(DEBUG_SERIAL)
                System.out.println("Der Blockingmode wurde eingeschaltet (siehe Verhalten der Methodes readLine()");

            this.os  = serialPort.getOutputStream();
            this.br  = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            this.is  = this.serialPort.getInputStream();
            this.dis = new DataInputStream(this.is);

            this.isOpen = true;
            return true;
        }
        else
        {
            this.isOpen = false;
            if(DEBUG_SERIAL)
                System.out.println("Die Schnittstelle konnte nicht geöffnet werden");
            return false;
        }
    }

    /**
     * schliesst die serielle Schnittstelle; die Schnittstelle ist dann 
     * nicht mehr verwendbar, bis sie wieder geoeffnet wird.
     */
    public void close()
    {
        if(DEBUG_SERIAL)
            System.out.println("Die Methode 'public void close()'' wurde aufgerufen");

        try
        {
            this.br.close();
            this.dis.close();
            this.is.close();
            if(this.serialPort.isOpen()){

                this.serialPort.closePort();
                this.isOpen = false;
            }
        }
        catch(Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler, beim Aufrufen in der Methode 'public void close()'\n" + e.toString());
        }
    }

    /**
     * liefert beim Aufruf die aktuelle Anzahl der Bytes zurueck, die beim Aufruf 
     * der Mehode von der seriellen Schnittstelle gelesen werden koennen. 
     * Die Methode ist nicht blockierend (nicht wartend).
     * 
     * @return aktuelle Anzahl der Bytes die eingelesen werden koennen.
     */
    public int dataAvailable()
    {
        if(DEBUG_SERIAL)
            System.out.println("Die Methode 'public int dataAvailable()' wurde aufgerufen...");
        try
        {
            int av = this.dis.available();
            if(DEBUG_SERIAL)
                System.out.println("... und liefert den Wert " + av + " zurück");
            return av;
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler, beim Aufrufen der Methode 'public int dataAvailable()'\n" + e.toString());
            return 0;
        }
    }  

    /**
     * liest ein Byte (0..255) von der seriellen Schnittstelle, bzw. liefert -1 
     * zurueck, wenn die Schnittstelle nicht geoeffnet ist oder keine Daten gelesen
     * werden koennen.
     * 
     *<b><font color=red>Zu pruefen, ob das korrekt ist 
     *Die Methode blockiert, bis mindestens ein Byte(wert) verfuegbar ist. ??? </font></b>
     * 
     * @return den eingelesenen Wert oder -1, wenn nichts zum Lesen vorhanden ist.
     */
    public int read()
    {
        if(DEBUG_SERIAL)
            System.out.println("Die Methode 'public int read()' wurde aufgerufen...");
        try
        {
            if(this.isOpen == false || dataAvailable() == -1)
            {
                return -1; 
            }

            while(dataAvailable() == 0 ); //blocking mode

            int rb = readByte();
            if(DEBUG_SERIAL)
                System.out.println("... und liefert den Wert " + rb + " zurück");
            return rb;
        }
        catch(Exception ioe)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler, beim Aufrufen der Methode 'public int read()'\n" + ioe.toString());
            return -1;
        }
    }

    /**
     * liest bis zu len Bytes von der seriellen Schnittstelle in das übergebene Byte-Array<br>
     * die Anzahl der tatsächlich gelesenen Bytes wird zurückgeliefert, bzw. liefert -1 zurück,<br>
     * wenn die Schnittstelle nicht geöffnet ist oder nicht gelesen werden konnte.
     * <b><font color=red>Zu pruefen, ob das korrekt ist <br>
     * Die Methode blockiert, bis mindestens ein Byte(wert) verfuegbar ist. ??? </font></b>
     * @param b das übergebene byte-Array. Dieses muss bereits deklariert und definiert worden sein (mit Mindestlänge 1)
     * @param len maximale Anzahl der Bytes, die gelesen werden sollen. Sind weniger Bytes verfügbar, werden nur diese in das Array übertragen 
     * @return tatsächliche Anzahl der in das byte-Array übertragene Werte oder -1, wenn nicht auf die Schnittstelle zugegriffen werden konnte. 
     */
    public int read(byte[] b, int len)
    {
        int readed = -1;
        while(dataAvailable() == 0); //blocking mode

        while(dataAvailable() > 0   &&
              len >= 0              &&
              b.length > readed+1       )
        {
            int rb  = readByte();
            if(rb >= 0)
                b[readed+1] = (byte) rb;
            else
                break;
            len--;
            readed++;
        }
        return readed;
    }

    /**
     * liest eine Zeile von der seriellen Schnittstelle, wenn Daten an der 
     * Schnittstelle verfuegbar sind und gibt diese als Zeichenkette zurueck. 
     * Wenn Daten verfuegbar sind, dann wird eine Zeile nur dann gelesen, wenn 
     * diese durch ein Zeilenendezeichen ("\n") abgeschlossen ist. 
     * Das Zeilenendezeichen wird nicht mit zurueckgegeben. Wenn Daten an der 
     * Schnittstelle verfuegbar sind, blockiert die Methode, bis eine komplette 
     * Zeile fertig eingelesen ist.
     * 
     * @return die eingelesene Zeile als String oder null, wenn nicht gelesen werden konnte.
     */
    public String readLine()
    {
        if(DEBUG_SERIAL)
            System.out.println("Die Methode 'public String readLine()' wurde aufgerufen");
        try
        {
            String rl = this.br.readLine();
            if(DEBUG_SERIAL)
                System.out.println("... und liefert die Zeilenkette  \n" + rl + " zurück");
            return rl;
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler beim Aufrufen der Methode 'public String readLine()'\n" + e.toString());
            return null;
        }
    }

    /**
     * schreibt ein Byte (0..255) auf die serielle Schnittstelle; wenn die 
     * Schnittstelle nicht geoeffnet ist, geschieht nichts.
     * 
     * @param value Byte welches gesendet werden soll.
     */
    public void write(int value)
    {
        try
        {
            writeByte(value);
        }
        catch (Exception ioe)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler, beim Aufrufen der Methode 'public void write(int value)'\n" + ioe.toString());
        }
    }  

    /**
     * schreibt einen String auf die serielle Schnittstelle; wenn die 
     * Schnittstelle nicht geoeffnet ist, geschieht nichts.
     * 
     * @param s String der gesendet werden soll.

     */
    public void write(String s)
    {
        try
        {
            this.os.write(s.getBytes());
            this.os.flush();
        }
        catch (Exception e)  
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler beim Aufrufen der Methode 'public void write(String s)'\n" + e.toString());
        }
    }

    /**
     * Diese Methode sendet das uebergebene Byte Array ueber die geoeffnete serielle Schnittstelle.
     * 
     * @param b Das Byte Array was gesendet werden soll.
     * @param len Anzahl der Bytes, die gesendet werden sollen.
     * Wenn nicht gesendet werden konnte, passiert nichts
     */
    public void write(byte[] b, int len)
    {
        try
        {
            this.os.write(b, 0, len);
        }catch (Exception e)  {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public void write(byte[] b, int len)' ist ein Fehler aufgetreten\n" + e.toString());}
    }

    /**
     * setzt den Status des Modem-Steuerausgangs RTS (request to send) auf arg.
     * True entspricht HIGH und false LOW.
     *
     * @param arg true wenn der RTS-Pegel auf High gesetzt werden soll oder false, wenn der RTS-Pegel auf Low gesetzt werden soll.
     */
    public void setRTS(boolean arg)
    {
        try
        {
            if (arg == true)
                this.serialPort.setRTS();
            else
                this.serialPort.clearRTS();
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public void setRTS(boolean arg)' ist ein Fehler aufgetreten\n" + e.toString());
        }
    }

    /**
     * setzt den Status des Modem-Steuerausgangs DTR (data terminal ready) auf arg.
     * True entspricht HIGH und false LOW.
     * 
     * @param arg true wenn der DTR-Pegel auf High gesetzt werden soll oder false, wenn der DTR-Pegel auf Low gesetzt werden soll.
     */
    public void setDTR(boolean arg)
    {
        try
        {
            if (arg == true)
                this.serialPort.setDTR();

            else
                this.serialPort.clearDTR();

        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public void setDTR(boolean arg)' ist ein Fehler aufgetreten\n" + e.toString());
        }
    }

    /**
     * liefert den Status des Modem-Steuereingangs CTS (clear to send). 
     * True wenn CTS auf High ist, sonst false.
     * 
     * @return den Wert von CTS. 
     */
    public boolean isCTS()
    {
        try
        {
            return this.serialPort.getCTS();
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public boolean isCTS()' ist ein Fehler aufgetreten\n" + e.toString());

            return false;
        }
    }

    /**
     * Diese Methode gibt den Status von der Steuerleitung DSR der seriellen Schnittstelle zurueck.
     * @return Der Wert von DSR. True wenn DSR gesetzt ist, ansonsten false.
     */
    public boolean isDSR()
    {
        try
        {
            return this.serialPort.getDSR();
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public boolean isDSR()' ist ein Fehler aufgetreten\n" + e.toString());
            return false;
        }
    }
    /**
     * ***************************************************************************
     * ENDE METHODEN HESSISCHES LANDESABITUR
     * ***************************************************************************
     */  

    /**
     * ***************************************************************************
     * WEITERE METHODEN ODER HILFSMETHODEN
     * ***************************************************************************
     */

    /**
     * Diese Methode gibt den Status der Steuerleitung RI (Ring Indicator) der seriellen Schnittstelle zurueck.
     * @return Der Wert von RI. True wenn RI gesetzt ist, ansonsten false.
     */
    public boolean getRI()
    {
        try
        {
            return this.serialPort.getRI();
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public boolean getRI()' ist ein Fehler aufgetreten\n" + e.toString());
            return false;
        }
    }

    /**
     * Diese Methode gibt den Status von der Steuerleitung DTR der seriellen Schnittstelle zurueck.
     * @return Der Wert von DTR. True wenn DTR gesetzt ist, ansonsten false.
     * <p>
     * Note that polling this line's status is not supported on Windows, so results may be incorrect.
     */
    public boolean getDTR()
    {
        try
        {
            return this.serialPort.getDTR();
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public boolean getDTR()' ist ein Fehler aufgetreten\n" + e.toString());
            return false;
        }
    }

    /**
     * Diese Methode gibt den Status von der Steuerleitung RTS der seriellen Schnittstelle zurueck.
     * @return Der Wert von RTS. True wenn RTS gesetzt ist, ansonsten false.
     */
    public boolean getRTS()
    {
        try
        {
            return this.serialPort.getRTS();
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public boolean getRTS()' ist ein Fehler aufgetreten\n" + e.toString());
            return false;
        }
    }


    /**
     * Diese Methode gibt den Status von der Steuerleitung DCD (Carrier Detect) der seriellen Schnittstelle zurueck.
     * @return Der Wert von DCD. True wenn DCD gesetzt ist, ansonsten false.
     */
    public boolean getDCD()
    {
        try
        {
            return this.serialPort.getDCD();
        }
        catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'public boolean getDCD()' ist ein Fehler aufgetreten\n" + e.toString());
            return false;
        }
    }

    /**
     * Diese Methode sendet den uebergebenen Byte ueber die geoeffnete serielle Schnittstelle.
     * Wenn nicht gesendet werden konnte, passiert nichts
     * 
     * @param wert das Byte das gesendet werden soll.
     */
    private void writeByte(int wert)
    {
        try
        {
            this.os.write(wert);
            this.os.flush();
        }catch (Exception e)  {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'private void writeByte(int wert)' ist ein Fehler aufgetreten\n" + e.toString());}
    }

    /**
     * Diese Methode sendet das übergebene Byte Array über die geöffnete serielle Schnittstelle.
     * Wenn nicht gesendet werden konnte, passiert nichts
     */
    public void writeBytes(byte[] dat)
    {
        try
        {
            this.os.write(dat);
        }catch (Exception e){
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'private void writeBytes(byte[] dat)' ist ein Fehler aufgetreten\n" + e.toString());}

    }

    /**
     * Diese Methode liest ein Byte von der geoeffneten seriellen Schnittstelle ein.
     * Konnte nichts gelesen werden, wird -1 returniert.
     * 
     * @return den eingelesenen Wert. 
     */
    public int readByte()
    {
        try
        {
            return this.is.read();
        }catch (Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'private int readByte()' ist ein Fehler aufgetreten\n" + e.toString());
        }
        return -1;
    }

    /**
     * liest mehrere Byte von der geoeffneten seriellen Schnittstelle ein.
     * 
     * @param length die Anzahl an Bytes die maximal gelesen werden soll.
     * Wenn weniger Bytes im Buffer sind wird ein kleineres Array zurueckgegeben.
     * @return ein Array mit den gelesenen Bytes. 
     * Konnte nichts gelesen werden, wird ein leeres byte-Array returniert.
     */
    public byte[] readBytes(int length)
    {
        try{
            byte data[] = new byte[length];
            int read = 0;
            read = this.is.read(data);
            if(read < 0){
                data = new byte[0];
            }
            else if(read < length){
                byte temp[] = data;
                data = new byte[read];
                for(int i = 0; i<read; i++){
                    data[i] = temp[i];
                }
            }
            return data;
        }
        catch(Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'private byte[] readBytes(int length)' ist ein Fehler aufgetreten\n" + e.toString());
            return new byte[0];
        }
    }

    /**
     * liest alle im Puffer befindlichen Daten ein.
     * 
     * @return ein Array mit den gelesenen Bytes. 
     * Konnte nichts gelesen werden, wird ein leeres byte-Array returniert.
     */
    public byte[] readBytes()
    {
        try{
            return this.readBytes(this.is.available());
        }
        catch(Exception e)
        {
            if(DEBUG_SERIAL)
                System.out.println("Fehler: Beim Schreiben in der Methode 'private byte[] readBytes()' ist ein Fehler aufgetreten\n" + e.toString());
            return new byte[0];
        }

    }

    /**
     * Diese Methode liest einen Byte von der geoeffneten seriellen Schnittstelle ein.
     * 
     * Wenn die andere Methode um einen Byte einzulesen Probleme macht bzw. immer 
     * falsche Werte zurueckgibt koennte diese Methode eine Alternative sein.
     * 
     * @throws IOException Wenn nicht von der Schnittstelle gelesen werden kann.
     * Dies passiert beispielsweise immer dann, wenn diese Methode nach close aufgerufen wird.
     * @return den eingelesenen Wert.
     */
    private int readUnsignedByte() throws IOException
    {
        return this.dis.readUnsignedByte();
    }

    /**
     * Diese Methode gibt den Status des Buffered Readers zurueck. 
     * Wenn diese Methode true zurueck liefert koennen Daten eingelesen werden.
     * 
     * @return true wenn Daten anliegen die eingelesen werden koennen, ansonsten false.
     */
    private boolean bufferedReaderReady()
    {
        try
        {
            return this.br.ready();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * ***************************************************************************
     * GETTER- und SETTER-METHODEN
     * ***************************************************************************
     */
    public void setPortName (String portName)
    {
        if(!isPortNameValid(portName))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in portName [" + portName +"]");
            System.exit(-1); //throw new Exception("PortName not valid!");
        }
        this.portName = portName;
    }

    public void setBaudrate(int baudrate)
    {
        if(!isBaudRateValid(baudrate))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in baudrate [" + baudrate +"]");
            System.exit(-2); //throw new Exception("BaudRate not valid!");
        }
      this.baudrate = baudrate;
    }

    public void setDataBits(int dataBits)
    {
        if(!isDataBitsValid(dataBits))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in dataBits [" + dataBits +"]");
            System.exit(-3); //throw new Exception("Databits not valid!");
        }
        this.dataBits = dataBits;
    }

    public void setStopBits(int stopBits)
    {
        if(!isStopBitsValid(stopBits))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in stopBits [" + stopBits +"]");
            System.exit(-4); //throw new Exception("Sopbits not valid!");
        }
        this.stopBits = stopBits;
    }
    
    public void setParity(int parity)
    {
        if(!isParityValid(parity))
        {
            if(DEBUG_SERIAL)
                System.out.println("Ungültiger Wert in parity [" + parity +"]");
            System.exit(-5); //throw new Exception("Parity not valid!");
        }
        this.parity=parity;
    }

    public String getPortName()
    {
        return portName;
    }

    public int getBaudrate()
    {
        return baudrate;
    }

    public int getDataBits()
    {
        return dataBits;
    }

    public int getStopBits()
    {
        return stopBits;
    }

    public int getParity()
    {
        return parity;
    }
    /**
     * ***************************************************************************
     * ENDE GETTER- und SETTER-METHODEN
     * ***************************************************************************
     */

    /**
     * ***************************************************************************
     * ENDE WEITERE METHODEN ODER HILFSMETHODEN
     * ***************************************************************************
     */

    /**
     * ***************************************************************************
     * HILFSMETHODEN
     * ***************************************************************************
     */
    private static int getnComPorts() {

        SerialPort[] sps = SerialPort.getCommPorts();

        int n = sps.length;

        for(int i = 0; i < n; i++){

            System.out.println("DescriptivePortName: " + sps[i].getDescriptivePortName());
            System.out.println("PortDescription:     " + sps[i].getPortDescription());
            System.out.println("SystemPortName:      " + sps[i].getSystemPortName());

        }
        return n;
    }

    private boolean isPortNameValid(String portName) {

        SerialPort[] sps = SerialPort.getCommPorts();

        for(int i = 0; i < sps.length; i++)
        {
            if(DEBUG_SERIAL) {

                System.out.println("DescriptivePortName: " + sps[i].getDescriptivePortName());
                System.out.println("PortDescription:     " + sps[i].getPortDescription());
                System.out.println("SystemPortName:      " + sps[i].getSystemPortName());

            }
            if(sps[i].getSystemPortName().equals(portName))
                return true;
        }
        return false;
    }

    private boolean isParityValid(int parity)
    {
        switch (parity)
        {
            case PARITY_NONE:
            case PARITY_EVEN:
            case PARITY_ODD:
            //case SPACE_PARITY:
            //case MARK_PARITY:
            return true;

            default:
            return false;
        }
    }

    private boolean isStopBitsValid(int stopbits)
    {
        switch(stopbits)
        {
            case STOPBITS_1:
            case STOPBITS_1_5:
            case STOPBITS_2:
            return true;
            default:
            return false;
        }
    }
  
    private boolean isDataBitsValid(int databits)
    {
        // Bug-Fix fuer Version 5_0_8 in Methode isDataBitsValid(int databits)
        // statt: databits >= DATABITS_5 || databits <= DATABITS_8
        // neu: databits >= DATABITS_5 && databits <= DATABITS_8
        if(databits >= DATABITS_5 && databits <= DATABITS_8)
            return true;
        return false;
    }

    private boolean isBaudRateValid(int baudrate)
    {
        return true;
    }

    /**
     * ***************************************************************************
     * ENDE HILFSMETHODEN
     * ***************************************************************************
     */
}

//BACKLOG
/*
public static final int FLOW_CONTROL_DISABLED            = SerialPort.FLOW_CONTROL_DISABLED;
public static final int FLOW_CONTROL_CTS_ENABLED         = SerialPort.FLOW_CONTROL_CTS_ENABLED;
public static final int FLOW_CONTROL_DSR_ENABLED         = SerialPort.FLOW_CONTROL_DSR_ENABLED;
public static final int FLOW_CONTROL_DTR_ENABLED         = SerialPort.FLOW_CONTROL_DTR_ENABLED;
public static final int FLOW_CONTROL_RTS_ENABLED         = SerialPort.FLOW_CONTROL_RTS_ENABLED;
public static final int FLOW_CONTROL_XONXOFF_IN_ENABLED  = SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED;
public static final int FLOW_CONTROL_XONXOFF_OUT_ENABLED = SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;

public static final int LISTENING_EVENT_DATA_AVAILABLE   = SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
public static final int LISTENING_EVENT_DATA_RECEIVED    = SerialPort.LISTENING_EVENT_DATA_RECEIVED;
public static final int LISTENING_EVENT_DATA_WRITTEN     = SerialPort.LISTENING_EVENT_DATA_WRITTEN;

public static final int NO_PARITY                        = SerialPort.NO_PARITY;
public static final int EVEN_PARITY                      = SerialPort.EVEN_PARITY;
public static final int ODD_PARITY                       = SerialPort.ODD_PARITY;
public static final int SPACE_PARITY                     = SerialPort.SPACE_PARITY;
public static final int MARK_PARITY                      = SerialPort.MARK_PARITY;

public static final int TIMEOUT_NONBLOCKING              = SerialPort.TIMEOUT_NONBLOCKING;
public static final int TIMEOUT_READ_BLOCKING            = SerialPort.TIMEOUT_READ_BLOCKING;
public static final int TIMEOUT_READ_SEMI_BLOCKING       = SerialPort.TIMEOUT_READ_SEMI_BLOCKING;
public static final int TIMEOUT_SCANNER                  = SerialPort.TIMEOUT_SCANNER;
public static final int TIMEOUT_WRITE_BLOCKING           = SerialPort.TIMEOUT_WRITE_BLOCKING;

public static final int ONE_STOP_BIT                     = SerialPort.ONE_STOP_BIT;
public static final int ONE_POINT_FIVE_STOP_BITS         = SerialPort.ONE_POINT_FIVE_STOP_BITS;
public static final int TWO_STOP_BITS                    = SerialPort.TWO_STOP_BITS;

public static void test(String portName) throws Exception
{
if(getnComPorts() == 0)
System.out.println("Keine serielle Schnittstellen vorhanden!");

Serial ssn = new Serial(portName,115200, 8, STOPBITS_1, PARITY_NONE);
if(ssn.open())
{
while(true)
{
try
{
if(ssn.dataAvailable() > 0)
System.out.println(ssn.readLine());
}
catch (Exception e) 
{
}
}
}

}  
 */
