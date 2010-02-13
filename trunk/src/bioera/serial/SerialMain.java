package bioera.serial;

import bioera.graph.designer.DesignException;
import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.*;
import bioera.properties.*;
import bioera.serial.*;
import bioera.graph.runtime.RuntimeFrame;
import bioera.graph.designer.*;




public class SerialMain extends Thread {


    public static SerialPortImpl impl = SerialPortFactory.getInstance("javax.comm");
    public static SerialPortImpl impl2 = SerialPortFactory.getInstance("javax.comm");
    public static int lastPortNum = -1;
    public static InputStream streamIn;
    public static OutputStream streamOut;
    public static String[] portsavail;
    public static boolean didConnect;
    public static boolean firstRun = true;

    public SerialMain() {

        streamIn = impl.in;
        streamOut = impl.out;
        try{init();}catch(Exception e){e.printStackTrace();}

        //return ports (own thread)

    }



public static void init() throws Exception {  //attempt a connection

    if(firstRun == true){
      new tryConnect().start();}
    else{

        //new initConn().start();
        try {
            String msg = impl.connect("COM" + String.valueOf(lastPortNum),
                                       "19200", "8", "1", "NONE", "NONE");
            if (msg != null) {
                throw new DesignException(msg);
            }
        } catch (Exception e) {
            try {Main.app.noDevice=true;
                throw new DesignException("Serial port initialization error: "+e);
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        try{impl.setDTR(true);impl.setRTS(true);}catch(Exception e){e.printStackTrace();}
        streamIn = impl.in;
        streamOut = impl.out;

    }

}



static class tryConnect extends Thread {

   tryConnect() {
   }

   public void run()  {

        try {
            impl2.close();
        } catch (Exception e) {
            e.printStackTrace();
        } //close before connecting

        //set current port options / used by SerialPort element's combobox
        portsavail = impl2.ports;

        try {
            String msg = impl2.connect("COM" + String.valueOf(lastPortNum),
                                       "19200", "8", "1", "NONE", "NONE");
            if (msg != null){
                throw new DesignException(msg);
                //return;
            }

            impl2.init();
        } catch (Exception e) {try{Main.app.noDevice=true;
            throw new DesignException("Serial port initialization error: " + e); }catch(Exception E){E.printStackTrace();}
        }
        firstRun=false;
        impl=impl2;
        try{impl.setDTR(true);impl.setRTS(true);}catch(Exception e){e.printStackTrace();}
        streamIn = impl2.in;
        streamOut = impl2.out;

    }

}



public static void getTables() throws Exception {

        try {

            //InputStream streamInt;  //new InputStream(Main.app.impl.in);
            //OutputStream streamOutt; //new OutputStream(Main.app.impl.out);
            //streamOutt = Main.app.impl.out;
            //streamInt = Main.app.impl.in;

            BufferedInputStream bufin = new BufferedInputStream(streamIn);
            int sleepbeforestart = 0;
            int sleepbetweentables = 10;
            int sleepbetweenbytes = 150;
            int[] getTable0cmd = {0xa3, 0x46, 0x20, 0x01, 0x09};
            //int[] getTable1cmd = {0xa3, 0x46, 0x07, 0x20, 0x01, 0x01, 0x11};

            Thread.sleep(sleepbeforestart);

            byte[] preSend = new byte[12];

            while (preSend[0] == 0) {
                bufin.read(preSend);
            }

            //send command to get table0
            for (int i = 0; i < getTable0cmd.length; i++) {
                streamOut.write(getTable0cmd[i]);
                Thread.sleep(sleepbetweenbytes);
            }
            bufin.read(Main.presageTable0);
            Thread.sleep(sleepbetweentables);

           
            
            /**send command to get table1
            for (int z = 0; z < getTable1cmd.length; z++) {
                streamOut.write(getTable1cmd[z]);
                Thread.sleep(sleepbetweenbytes);
            }
            bufin.read(Main.presageTable1);
            **/
            if (Main.presageTable0[0] != 0) {
                Main.app.runtimeFrame.changeConnIcon(1);
                Main.gotTables = true;

                try {
                    Main.app.runtimeFrame.startMenuItem.setEnabled(true);
                    Main.app.runtimeFrame.setToolBarButton("Start", true);
                    Main.app.runtimeFrame.setToolBarButton("Stop", false);
                    Main.app.runtimeFrame.setToolBarButton("Table Editor", true);
                } catch (Exception ex) {}
            } else {
                try {
                    Main.app.runtimeFrame.changeConnIcon(0);
                    Main.gotTables = false;
                    Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                    Main.app.runtimeFrame.setToolBarButton("Start", false);
                    Main.app.runtimeFrame.setToolBarButton("Stop", true);
                    Main.app.runtimeFrame.setToolBarButton("Table Editor", false);
                } catch (Exception ex) {}
            }

            for (int i = 0; i < 8; i++) { //set sensitivity table
                int m1 = (i * 2) + 5;
                int m2 = i + 21;
                int m3 = i + 29;
                int m4 = i + 37;
                Main.sensValues[0][i] = ((((Main.presageTable0[m1]) & 0xff) <<
                                          8) + (Main.presageTable0[m1 + 1]) &
                                         0xff);
                Main.sensValues[1][i] = ((Main.presageTable0[m2]) & 0xff);
                Main.sensValues[2][i] = ((Main.presageTable0[m3]) & 0xff);
                Main.sensValues[3][i] = ((Main.presageTable0[m4]) & 0xff);            
            }

        }

        catch (Exception ex) {
            ex.printStackTrace();
            Main.gotTables = false;
        }

    }


public static void getStatus() throws Exception {

    try {


        BufferedInputStream bufin = new BufferedInputStream(streamIn);
        int sleepbeforestart = 0;
        int sleepbetweentables = 10;
        int sleepbetweenbytes = 150;
        int[] getStatuscmd = {0xa3, 0x46, 0x21, 0x01, 0x0a};

        Thread.sleep(sleepbeforestart);

        byte[] preSend = new byte[12];

        while (preSend[0] == 0) {
            bufin.read(preSend);
        }

        //send command to get table0
        for (int i = 0; i < getStatuscmd.length; i++) {
            streamOut.write(getStatuscmd[i]);
            Thread.sleep(sleepbetweenbytes);
        }
        bufin.read(Main.presageStatus);
        Thread.sleep(sleepbetweentables);

       
        
        /**send command to get table1
        for (int z = 0; z < getTable1cmd.length; z++) {
            streamOut.write(getTable1cmd[z]);
            Thread.sleep(sleepbetweenbytes);
        }
        bufin.read(Main.presageTable1);
        **/
        if (Main.presageStatus[0] != 0) {
            Main.app.runtimeFrame.changeConnIcon(1);
            Main.gotStatus = true;

            try {
                Main.app.runtimeFrame.startMenuItem.setEnabled(true);
                Main.app.runtimeFrame.setToolBarButton("Start", true);
                Main.app.runtimeFrame.setToolBarButton("Stop", false);
                Main.app.runtimeFrame.setToolBarButton("Table Editor", true);
            } catch (Exception ex) {}
        } else {
            try {
                Main.app.runtimeFrame.changeConnIcon(0);
                Main.gotStatus = false;
                Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                Main.app.runtimeFrame.setToolBarButton("Start", false);
                Main.app.runtimeFrame.setToolBarButton("Stop", true);
                Main.app.runtimeFrame.setToolBarButton("Table Editor", false);
            } catch (Exception ex) {}
        }
/**   (?)get mode and probe config here 
 * 
        for (int i = 0; i < 8; i++) { //set sensitivity table
            int m1 = (i * 2) + 5;
            int m2 = i + 21;
            int m3 = i + 29;
            int m4 = i + 37;
            Main.sensValues[0][i] = ((((Main.presageTable0[m1]) & 0xff) <<
                                      8) + (Main.presageTable0[m1 + 1]) &
                                     0xff);
            Main.sensValues[1][i] = ((Main.presageTable0[m2]) & 0xff);
            Main.sensValues[2][i] = ((Main.presageTable0[m3]) & 0xff);
            Main.sensValues[3][i] = ((Main.presageTable0[m4]) & 0xff);            
        }
**/
    }

        
    catch (Exception ex) {
        ex.printStackTrace();
        Main.gotStatus = false;
    }

}



    class DataThread extends Thread {


        BufferedInputStream bufin = new BufferedInputStream(streamIn);
            BufferedReader inStream;

            PrintStream pStream;

            /** Construct this object */
            DataThread(InputStream is, PrintStream os) {
                inStream = new BufferedReader(new InputStreamReader(is));
                pStream = os;
            }

            DataThread(BufferedReader is, PrintStream os) {
                inStream = is;
                pStream = os;
            }

            /** A Thread's run method does the work. */
            public void run() {
                byte ch = 0;
                try {
                    while ((ch = (byte) inStream.read()) != -1)
                        pStream.print((char) ch);
                } catch (IOException e) {
                    System.err.println("Input or output error: " + e);
                    return;
                }
            }
    }

}
