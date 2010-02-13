/* Presage.java v 1.0.9   1/25/06 7:15 PM
 *
 *
 */

package bioera.device.impl;

import bioera.processing.*;
import bioera.fft.*;
import java.util.*;



public final class Presage extends Element {
	private final static String propertiesDescriptions[][] = {
	};

        public final static int HEADER_1 = 0xa3; // always - decimal 163
        public final static int HEADER_2_16ds = 0x48; // 16bit datastream - decimal  72
        public final static int HEADER_2_Cmd = 0x46; // command
        public final static int HDRCmdStart = 0x23; // start command
        public final static int HEADER_2_24ds = 0x47; // 24bit datastream (disabled)
        public final static int HDRSlSnstv = 0x49; // Single mode sensitivity
        public final static int HDRDlSnstv = 0x4A; // Dual mode sensitivity
        public final static int HDRSesDtls = 0x4B; // Start of session data
        public final static int HDREndses = 0x4C; // End of session data
        public final static int HDRicolor = 0x4D; // New Index/Color
        public final static int HDRok = 0x10; // CMD received OK
        public final static int pktlth = 12; // Standard Packet Length
        public final static int[] cmdStart = {HEADER_1, HEADER_2_Cmd,0x06,HDRCmdStart,0x01,0x12};
        public final static int[] sessionDetails = {};
        private boolean xxx = false;
	private int packetCounter = -1;
	private int packetsLost = 0;
	private int processedPackets = 0;

	private BufferedScalarPipe in;
	private ScalarPipeDistributor out[];

	private int buffer[];

        private int iInd = 0;
        private int iRed = 0;
        private int iGrn = 0;
        private int iBlu = 0;
        private int iTbd1 = 0;
        private int iTbd2 = 0;
        private int iTbd3 = 0;
        private int iTbd4 = 0;
	protected static boolean debug = bioera.Debugger.get("device.Presage");

public Presage() {
	super();

	setName("Presage");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	in.setBufferSize(17*256*4);  // Keep up to 4 seconds of data
	buffer = in.getBuffer();
	outputs = out = new ScalarPipeDistributor[6];
	for (int i = 0; i < out.length - 1; i++){
		out[i] = new ScalarPipeDistributor(this);
		out[i].setName("ch" + i);
	}

	out[4] = new ScalarPipeDistributor(this);
	out[4].setName("OUT");
        out[5] = new ScalarPipeDistributor(this);
        out[5].setName("IC");


}
public String getElementDescription() {
	return "Translates stream from protocol into data channels";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return 6;
}

public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}


static void prt(String s){
    System.out.println(s);
}
static void prtint(int n){
    System.out.println(n);
}

public final void process() throws Exception {

	int n = in.available();
	//if (n < 20)
	//	return;

	if (packetCounter == -1) {
            //Thread.sleep(4000);
            searchPacketBegin();
	}

	while (in.available() >= pktlth) {
    		//if (buffer[0] != HEADER_1 || buffer[1] != HEADER_2_16ds)
		if (buffer[0] != HEADER_1)
                {
                    packetsLost++;
                    {for(int i = 0; i < pktlth; i++){    prtint(buffer[i])   ;}}
                    prt("Total frames lost in Presage: " + packetsLost);
                    searchPacketBegin();
                    continue;
		}

                /*if ((buffer[0] == HEADER_1) && (buffer[1] == HDRSesDtls)){
                    int[] sessionDetails = {buffer[2],buffer[3],buffer[4],buffer[5],
                                           buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11], };
                    for (int i = 0; i < 10; i++){
                        prt("test test" + buffer[i] + " ");
                    }


                    //getSesDetails();
                }*/

                if ((buffer[0] == HEADER_1) && (buffer[1] == HDRicolor)){
                    //{for(int i = 0; i < pktlth; i++){    prtint(buffer[i])   ;}}
                    sendIndexColor(n);}

                if ((buffer[0] == HEADER_1) && (buffer[1] == HEADER_2_16ds)){
                    sendChannelData(n);}

                prtint(packetCounter);
                processedPackets++;
	}
}

public void reinit() throws Exception {
	packetCounter = -1;
	super.reinit();
}

public void sendChannelData(int n) throws Exception {
    for (int i = 0; i < 4; i++){
        if (out[i].isConnected())
            {
                n = (buffer[2 + (i<<1)] << 8) + buffer[3 + (i<<1)];
                out[i].write(n);
            }
      }
      packetCounter++;
      in.purge(pktlth);
}


public void sendIndexColor(int n) throws Exception {
    int iInd = buffer[2]; int iRed = buffer[3]; int iGrn = buffer[4]; int iBlu = buffer[5];
    int iTbd1 = buffer[6]; int iTbd2 = buffer[7]; int iTbd3 = buffer[8]; int iTbd4 = buffer[9];
    if (out[5].isConnected()){
        out[5].write(iInd);out[5].write(iRed);out[5].write(iGrn);out[5].write(iBlu);
        out[5].write(iTbd1);out[5].write(iTbd2);out[5].write(iTbd3);out[5].write(iTbd4);
    }
    packetCounter++;
    in.purge(pktlth);
}

public boolean iscmdReceived(boolean rcv){
    for (int i = 0; i < 14; i++) {
        if ((buffer[i] == HEADER_1) && (buffer[i + 1] == HEADER_2_16ds) &&
            (buffer[i + 3] == HDRok)) {
            in.purge(i);
            return true;
        }
    }
    return false;
}

 public void sendStartCMD() throws Exception {
    do {
        Thread.sleep(5000);
        if (out[4].isConnected()) {for(int i = 0; i < 6; i++){out[4].write(cmdStart[i]);}}    }
    while (iscmdReceived(false));
}


private final void searchPacketBegin() throws Exception {
/*        for (int i = 0; i < pktlth; i++){
        if ((buffer[i] == 0) && (buffer[i + 1] == 0)){
               in.purge(i);

               return;}
    }
    for (int i = 0; i < 24; i++){
        if ((buffer[i] == HEADER_1) && (buffer[i + 1] == HDRok)){
               in.purge(i);
               return;}
    }
*/
    for (int i = 0; i < 13; i++){
        if ((buffer[i] == HEADER_1) && (buffer[i + 1] == HEADER_2_16ds)){
            in.purge(i);
            return;}
	}

        String msg = "Presage header not found in " + pktlth + " bytes: ";
	for (int i = 0; i < pktlth; i++){
		msg += "" + buffer[i] + " ";
                in.purge(i);
	}
	in.purgeAll();
        prt(msg);
	//throw new Exception(msg);
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start() throws Exception {
    sendStartCMD();
    packetCounter = -1;
    in.purgeAll();
}
}
