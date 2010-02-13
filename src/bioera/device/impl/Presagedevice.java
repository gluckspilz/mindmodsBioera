/* Presagedevice.java v 1.0.9   1/25/06 7:15 PM
 *
 *
*/

package bioera.device.impl;

import java.util.*;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import bioera.Log;
import bioera.Main;
import bioera.graph.runtime.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.VolatileSettings;
import java.io.File;
import java.awt.Color;

public final class Presagedevice extends Element {


    private final static String propertiesDescriptions[][] = {

    };


    private final static int HEADER_1 = 0xa3; // always - decimal 163
    private final static int HEADER_2_16ds = 0x48; // 16bit datastream - decimal  72
    private final static int HEADER_2_Cmd = 0x46; // command
    private final static int HDRCmdReqTable = 0x20; // start command
    private final static int HDRCmdStart = 0x23; // start command
    private final static int HDRCmdStop = 0x24; // start command
    private final static int HDRSlTrgt = 0x49; // Single Probe Target Params
    private final static int HDRDlTrgt = 0x4A; // Dual Probe Target Params
    private final static int HDRSesDtls = 0x4B; // Start of session details
    private final static int HDREndses = 0x4C; // End of session data
    private final static int HDRIdx = 0x4D; //
    private final static int HDRok = 0x10; // CMD received OK
    private final static int HDRError = 0x4E; //error has halted system
    private final static int pktlth = 12; // Standard Packet Length
    private final static int sleeptime = 0000; //pause time before sending start session
    private final static int[] cmdStart = {HEADER_1, HEADER_2_Cmd, HDRCmdStart, 0x01, 0x0c};
    private final static int[] cmdStop = {HEADER_1, HEADER_2_Cmd, HDRCmdStop, 0x01, 0xd};
    private final static int[] cmdReqTable = {HEADER_1, HEADER_2_Cmd, HDRCmdReqTable, 0x01, 0x09};
    private static BufferedScalarPipe in;
    private static ScalarPipeDistributor out[];
    private static boolean loadSession=false;
    private static boolean sessionActive = false;
    private int buffer[];
    private int[] EosDtls = {0,0,0,0,0,0,0,0,0,0,0,0};
    private int[] SesDtls = {0,0,0,0,0,0,0,0,0,0,0,0,0};
    private int[] Trgt = {0,0,0,0,0,0,0,0,0,0,0,0};
    private int[] FirstTrgt = {0,0,0,0,0,0,0,0,0,0,0,0};
    private int[] SessionData = {0,0,0,0,0,0,0,0,0,0};
    private int[] Idx = {0,0,0,0,0,0,0,0,0,0,0,0};
    private long scanTimer = 0;
    private int startAttempts = 0;
    private int packetCounter = -1;
    private int packetsLost = 0;
    private int processedPackets = 0;
    //public static long sessionStartTime=0;
    private static Log sessFile;

    int sessFileIndex = 0;
    int sessFiledpkt[] = new int[16];
    long elapsed = 0;
    private boolean tempC = false;
    private boolean calibrate = false;
    private long calibrateTimer = 0;
    private boolean firstSendSessionInfo=true;
    private int startSens = 0;
    private int endSens = 0;
    private int startSensIdx = 0;
    private int EndSensIdx = 0;
    private int sensIdxTrav = 0;
    private int lowestMeas[] = new int[4];
    private int highestMeas[] = new int[4];
    private int totalforAverage[] = new int[4];
    private int measAverage[] = new int[4];
    private int numforAverage = 0;
    protected static boolean debug = bioera.Debugger.get("device.Presage");



    public Presagedevice() {
        super();

        setName("PresageD");
        in = (BufferedScalarPipe) inputs[0];
        in.setName("IN");
        in.setBufferSize(17 * 256 * 4);
        buffer = in.getBuffer();
        outputs = out = new ScalarPipeDistributor[10];
        for (int i = 0; i < out.length - 1; i++) {
            out[i] = new ScalarPipeDistributor(this);
            out[i].setName("ch" + i);
        }
        out[4] = new ScalarPipeDistributor(this);
        out[4].setName("OUT");
        out[5] = new ScalarPipeDistributor(this);
        out[5].setName("SES");
        out[6] = new ScalarPipeDistributor(this);
        out[6].setName("SENS");
        out[7] = new ScalarPipeDistributor(this);
        out[7].setName("TIME");
        out[8] = new ScalarPipeDistributor(this);
        out[8].setName("SESDATA");
        out[9] = new ScalarPipeDistributor(this);
        out[9].setName("IDX");


    }

    public String getElementDescription() {
        return "Translates Presage stream from protocol into data channels";
    }

    public int getInputsCount() {
        return 1;
    }

    public int getOutputsCount() {
        return 10;
    }

    public Object[] getPropertyDescription(String name) {
        Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
        if (ret == null) {
            return super.getPropertyDescription(name);
        }
        return ret;
    }


    static void prt(String s) {
        System.out.print(s);
    }

    static void prtint(int n) {
        System.out.print(n);
    }

    public final void process() throws Exception {
        int n = in.available();


        //Start the Session
        if ((sessionActive == false) && (loadSession == false)) {
            if (startAttempts == 0) in.purgeAll(); buffer=in.getBuffer();
            if (startAttempts < 4) { //System.out.println("start attempt # ="+startAttempts);
                startSession(buffer);
            } else {
                      Main.app.runtimeFrame.errorDialog(99);   //Error Dialog - not responding to start command
                }
            }


        //Process Packets during Live Session
        if ((sessionActive == true) && (loadSession == false)) {
            //parse packets, write log, etc
            while (in.available() >= pktlth) {
                liveSession(buffer);
            }
        }

        //Read Session File and Display Data
        if ((loadSession == true)) {

            //all processing takes place at once in graph/chart display element

        }



        if (calibrate==true){
            if (calibrateTimer == 0) { //push on first datapacket
                Main.runtimeFrame.calibrateButton.setText("Calibrating Sensors");
                Main.runtimeFrame.calibrateButton.setForeground(Color.red);
                Main.runtimeFrame.calibrationIndicator();
                calibrateTimer = System.currentTimeMillis();
                return;
            }
            if ( (System.currentTimeMillis() > calibrateTimer+1000) ) {
                    Main.runtimeFrame.calibrationIndicator();
                calibrateTimer = System.currentTimeMillis();
            }
        }


    }


    private void liveSession(int[] buff) throws Exception {

        elapsed = (System.currentTimeMillis() - Main.app.sessionStartTime);  //calc elapsed for packet timestamps
        if (buff[0] != HEADER_1) {
            in.purge(1);
            return;
        }
        if (packetCounter == -1) {
            if ((buff[0] == HEADER_1) && (buff[3] == HDRok)) {
                in.purge(cmdStart.length);
            }
            if ((buff[0] == HEADER_1) && (buff[1] == HDRSesDtls)) {  //start of session details
                SesDtls = getSesDtls(buff);
                calibrate=false; //calibration complete when start of session pkt arrives
                Main.app.runtimeFrame.doSensMeter(SesDtls[2]-1, SesDtls[4]);
                //set session start time when session info arrives
                //sessionStartTime = System.currentTimeMillis();   now set from dyndisplayelement
                writeSessFile(buff);
            }
        }

        if ((buff[0] == HEADER_1) && (buff[1] == HDRError)){ //an error has halted the system
            Main.app.runtimeFrame.errorDialog(buff[2]);
        }


        if ((buff[0] == HEADER_1) && (buff[1] ==HDRIdx)){ //index packet (index + RGB);
            sendIndexInfo();
        }

        if ((buff[0] == HEADER_1) &&
            ((buff[1] == HDRDlTrgt) || (buff[1] == HDRSlTrgt))) {  //target packet
            Trgt = getTrgt(buff);
            sendSessionInfo();
            if (processedPackets < 10) {
                                //writeSessFile(buff);

                FirstTrgt = getTrgt(buff);
            }
            else{  //increase sensmeter one level
                Main.app.runtimeFrame.doSensMeter(SesDtls[2]-1);
            }
        }

        if ((buff[0] == HEADER_1) && (buff[1] == HEADER_2_16ds)) {  //data packet

          //get one packet for processing
          int[] snglPkt = new int[pktlth];
          for (int z = 0; z < pktlth; z++) {
              snglPkt[z] = buffer[z];
          }

            sendChannelData(snglPkt);
            writeSessFile(snglPkt);
        }

        if ((buff[0] == HEADER_1) && (buff[1] == HDREndses)) {  //end of session packet
            EosDtls = getEosDtls(buff);
            writeSessFile(FirstTrgt); //write first target packet to file
            writeSessFile(Trgt); //write last target packet to file
            writeSessFile(buff); //write end of session packet to files
            sessFile.close();
            EndSensIdx  = EosDtls[3];     //get end sensindx
            endSens = ( ((buff[4]& 0xff)*256) +(buff[5]& 0xff) ); //get end sens
            sensIdxTrav = (EndSensIdx - startSensIdx); //get traversed indexes
            for(int i=0;i<4;i++){
                measAverage[i] = ( totalforAverage[i]/numforAverage ); //get avg
                System.out.println("highest = "+i+" is"+highestMeas[i]);
                System.out.println("lowest = "+i+" is"+lowestMeas[i]);
                System.out.println("measAverage = "+i+" is"+measAverage[i]);
            }

            loadSession = false;
            Main.app.runtimeFrame.atEOS(startSens, endSens, sensIdxTrav, highestMeas, lowestMeas, measAverage, SesDtls[3]); //stopprocessor, rename currentfile to completed, spawn save dialog, enable save menu option
            in.purgeAll();


        }
        processedPackets++;


        //writeSessFile(buff); //write packet to session file
        in.purge(pktlth);
    }




public static void loadSession() {
    sessionActive=true;loadSession=true;
}


    public void startSession(int[] buff) throws Exception {

        if ((scanTimer == 0) || (scanTimer < System.currentTimeMillis())) {
            for (int j = 0; j < 6; j++) {
                try {
                    Thread.sleep(20); //pause between bytes sent
                    out[4].write(cmdStart[j]);
                } catch (Exception e) {
                    System.out.println("Cannot send start command to Presage");
                }
            }
            startAttempts++;
            scanTimer = System.currentTimeMillis() + 5000;
        }

        //was start cmd received?
        if (scanTimer > System.currentTimeMillis()) {
            if (buff[0] != HEADER_1) {
                in.purge(1);
            } else {
                if ((buff[1] == HEADER_2_Cmd) && (buff[2] == 0x06) &&
                    (buff[3] == HDRok)) {
                    in.purge(cmdStart.length);
                    sessionActive = true;
                    calibrate=true; //begin sensor calibration
                }
            }
        }
    }

    public static void sendStopCMD()  {
        for (int k = 0; k < 6; k++) {
            try {
                Thread.sleep(60); //pause between bytes sent
                out[4].write(cmdStop[k]);
            } catch (Exception e) {
                //System.out.println("Cannot send stop command to Presage");
            }
        }
    }


    private void writeSessFile(int[] buff) throws Exception {
        for (int l = 0; l < pktlth; l++) {
            int p = buff[l];
            sessFile.write((byte) (p));
        }
        long elapsed = (System.currentTimeMillis() - Main.app.sessionStartTime);
        sessFile.write((byte) (elapsed >> 24));
        sessFile.write((byte) (elapsed >> 16));
        sessFile.write((byte) (elapsed >> 8));
        sessFile.write((byte) (elapsed));
    }

    public int[] getSesDtls(int[] buf) throws Exception {
        for (int m = 0; m < pktlth; m++) {
            SesDtls[m] = (buf[m]);
        }
        if ( (SesDtls[2]>2) ) { Main.app.dualProbe=true; }  //used by runtimeeventhandler
        else {Main.app.dualProbe=false;}
        return SesDtls;
    }

    private int[] getTrgt(int[] buf) throws Exception {
        for (int n = 0; n < pktlth; n++) {
             Trgt[n] = (buf[n]);
        }
        return Trgt;
    }

    private int[] getEosDtls(int[] buf) throws Exception {
        for (int o = 0; o < pktlth; o++) {
            EosDtls[o] = (buf[o]);
        }
        return EosDtls;
    }

    private int rCalc(int n) throws Exception {
        double r;
        Integer N = new Integer(n);
        r = (( (N.doubleValue()*100000)/(65535-N.doubleValue()) )/1000);
        r = Math.round(r);
        Double R = new Double(r);
        int p = R.intValue();
        return p;
    }

    private int tCalc(int n) throws Exception {
        double k;
        int it=0;
        double tempd=0;
        int tempi=0;
        Integer N = new Integer(n);
        k = (( (N.doubleValue()*100000)/(65535-N.doubleValue()) /1000 ));
        while (Main.app.tlookup[it]>k){it++;}
        tempd = ( (it+10) + ( ( (Main.app.tlookup[it])-k) / (Main.app.tlookup[it] - Main.app.tlookup[it+1]))   );
        tempd = tempd*100;
        Double T = new Double(tempd);
        tempi = T.intValue();
        return tempi;
    }

    private void sendIndexInfo() throws Exception {
        for (int z = 0; z < pktlth; z++) {
            out[9].writeByte((byte)buffer[z]);
        }
    }

    private void sendSessionInfo() throws Exception {
        for (int j = 0; j < 3; j++) {
            if (out[5].isConnected()) {
               SessionData[j] = rCalc((Trgt[2 + (j << 1)] << 8) + Trgt[3 + (j << 1) ]);
            }
        }
        SessionData[3]=((Trgt[8]*256)+(Trgt[9]));  //get sensitivity level
        SessionData[4]=SesDtls[2]-1; //get mode
        SessionData[5]=SesDtls[3]; //get probeconf
        SessionData[6]=SesDtls[4]; //get sensindx
        SessionData[7]=((SesDtls[5]*256)+(SesDtls[6])); //get sesslength
        SessionData[9]=(SesDtls[7]); //get samplerate

        if (tempC==true)SessionData[8]=0;
        else {SessionData[8]=1;}

        int n;
        for (int k = 0; k < 10; k++) {
            if (out[5].isConnected()) {
                n = SessionData[k];
                //System.out.print(SessionData[i]+" "); // debug:print sessioninfopacket
                out[5].write(n);}
        }

             if (out[6].isConnected()) {out[6].write((Trgt[8]*256)+(Trgt[9]));}
             //set the runtime status bar
             Main.app.runtimeFrame.statusLengthf.setText( String.valueOf(SessionData[7]) );  //seslength
             Main.app.runtimeFrame.statusModef.setText( String.valueOf(SessionData[4]+1) );  //mode
             Main.app.runtimeFrame.statusSensf.setText( String.valueOf(SessionData[3])+"%" );  //sensitivity
             Main.app.runtimeFrame.statusProbef.setText( String.valueOf(SessionData[5]) );  //probeconf
             //prepare for eos
             if( firstSendSessionInfo==true ){
                 startSens = SessionData[3];
                 startSensIdx = SessionData[6];
                 firstSendSessionInfo=false;
             }
         }


         private void sendChannelData(int[] buf) throws Exception { //normal sendChannelData for live session
             int n = 0;
             for (int x = 0; x < 4; x++) {

                 n = (buf[(2 + (x << 1))] << 8) + buf[(3 + (x << 1))];
                 if (x % 2 == 0) {
                     out[x].write(rCalc(n));
                 } else {
                     out[x].write(tCalc(n));
                 }


                 if (x % 2 == 0) {  //gsr chns
                     int temp = rCalc(n);
                     if (temp > highestMeas[x]) {
                         highestMeas[x] = temp;
                     }
                     if (temp < lowestMeas[x]) {
                         lowestMeas[x] = temp;
                     }
                     totalforAverage[x] = totalforAverage[x] + temp;
                 }
                 else{   //tmp chans
                     int temp = tCalc(n);
                     if (temp > highestMeas[x]) {
                         highestMeas[x] = temp;
                     }
                     if (temp < lowestMeas[x]) {
                         lowestMeas[x] = temp;
                     }
                     totalforAverage[x] = totalforAverage[x] + temp;
                 }

             }
             numforAverage++;
             if (out[7].isConnected()) { //send timestamp if TIME is connected
                 out[7].write((int) elapsed);
             }
             packetCounter++;
         }


                //printpackets
                /*
                             for (int i = 0; i < pktlth; i++){
                    int p = buffer[i];
                    prtint(p);prt(" ");}
                         System.out.println("");*/


    public static void setDebug(boolean newValue) {
            debug = newValue;
    }


    public void reinit() throws Exception {
        scanTimer = 0;  startAttempts = 0;  packetCounter = -1; calibrate=false; calibrateTimer=0; loadSession = false;
        //Main.app.calibrating=false;
        //tempC = Main.app.tempC;
        super.reinit();

    }


    public void start() throws Exception {
        startSens = 0;  endSens = 0;   startSensIdx = 0;EndSensIdx = 0;numforAverage = 0;sensIdxTrav = 0;

        for(int i=0;i<4;i++){highestMeas[i] = 0;}
        for(int i=0;i<4;i++){lowestMeas[i] = 65536;}
        for(int i=0;i<4;i++){totalforAverage[i] = 0;}
        for(int i=0;i<4;i++){measAverage[i] = 0;}
        firstSendSessionInfo=true;
        scanTimer = 0;  startAttempts = 0;  packetCounter = -1; calibrate=false; calibrateTimer=0; sessionActive = false; //loadSession = false;
        in.purgeAll();buffer = in.getBuffer();
        // Open session log file
	sessFile = new Log(Main.app.designToSave);
        //try{Thread.sleep(sleeptime);}catch(Exception e){}
    }

    public void stop() throws Exception {
        sessFile.close();
        in.purgeAll();
    }

}
