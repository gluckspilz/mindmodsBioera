/* TS2.java v 1.0.9   1/25/06 7:15 PM
 *
 *
 */

package bioera.device.impl;

import bioera.processing.*;
import bioera.fft.*;


public final class TS2 extends Element {
	private final static String propertiesDescriptions[][] = {
	};

        public final static int HEADER_1 = 0xa3; // always - decimal 163
        public final static int HEADER_2_16ds = 0x48; // 16bit datastream - decimal  72
        public final static int HEADER_2_Cmd = 0x46; // command
        public final static int HDRCmdStart = 0x23; // start command
        public final static int HEADER_2_24ds = 0x47; // 24bit datastream (disabled)
        public final static int HDRSlSnstv = 0x49; // Single mode sensitivity
        public final static int HDRDlSnstv = 0x4A; // Dual mode sensitivity
        public final static int HDRStrtses = 0x4B; // Start of session data
        public final static int HDREndses = 0x4C; // End of session data
        public final static int HDRicolor = 0x4D; // New Index/Color
        public final static int HDRok = 0x10; // CMD received OK
        public final static int[] cmdStart = {HEADER_1, HEADER_2_Cmd,0x06,HDRCmdStart,0x01,0x12};
	private int packetCounter = -1;
	private int packetsLost = 0;
        private int packetsIndex = 0;
	private int processedPackets = 0;

	private BufferedScalarPipe in;
	private ScalarPipeDistributor out[];
	private int buffer[];
        //public int index[];
        /*
        public int iInd = index[0];
        public int iRed = index[1];
        public int iGrn = index[2];
        public int iBlu = index[3];
        public int iTbd1 = index[4];
        public int iTbd2 = index[5];
        public int iTbd3 = index[6];
        public int iTbd4 = index[7];
        */
	protected static boolean debug = bioera.Debugger.get("device.ts2");

public TS2() {
	super();

	setName("TS2");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	in.setBufferSize(17*256*4);  // Keep up to 4 seconds of data
	buffer = in.getBuffer();
	outputs = out = new ScalarPipeDistributor[5];
	for (int i = 0; i < out.length - 1; i++){
		out[i] = new ScalarPipeDistributor(this);
		out[i].setName("ch" + i);
	}

	out[4] = new ScalarPipeDistributor(this);
	out[4].setName("OUT");
}
public String getElementDescription() {
	return "Translates stream from protocol into data channels";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return 5;
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
		searchPacketBegin();
	}

	while (in.available() >= 12) {
		if (buffer[0] != HEADER_1 || buffer[1] != HEADER_2_16ds)
                {
                    packetsLost++;
                    prt("Total frames lost in ts2: " + packetsLost);
                    searchPacketBegin();
                    continue;
		}
                /*
                if (buffer[0] == HEADER_1 || buffer[1] == 0x4D)
                {
                    index[0] = buffer[2];
                    index[1] = buffer[3];
                    index[2] = buffer[4];
                    index[3] = buffer[5];
                    index[4] = buffer[6];
                    index[5] = buffer[7];
                    index[6] = buffer[8];
                    index[7] = buffer[9];
                    System.out.println("index= "+index[0]+" Red= "+index[1]+" Green="+index[2]+" Blue="+index[3]+" TBD1="+index[4]+" TBD2="+index[5]+" TBD3="+index[6]+" TBD4="+index[7] );
                    packetsIndex++;
                    searchPacketBegin();
                    continue;
                }*/
		processedPackets++;

		for (int i = 0; i < 4; i++){
			if (out[i].isConnected())
                            {
                                n = (buffer[2 + (i<<1)] << 8) + buffer[3 + (i<<1)];
				out[i].write(n);
                            }
		}
                packetCounter = (packetCounter + 1) % 256;
                prtint(packetCounter);
                in.purge(12);
	}
}

public void reinit() throws Exception {
	packetCounter = -1;
	super.reinit();
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
        Thread.sleep(3000);
        if (out[4].isConnected()) {for(int i = 0; i < 6; i++){out[4].write(cmdStart[i]);}}    }
    while (iscmdReceived(false));
}



private final void searchPacketBegin() throws Exception {
	for (int i = 0; i < 13; i++){
		if (
                                  (buffer[i] == HEADER_1) && (buffer[i + 1] == HEADER_2_16ds)
                   )
                {
			in.purge(i);
			return;
		}
	}

	String msg = "TS2 header not found in " + 15 + " bytes: ";
	for (int i = 0; i < 15; i++){
		msg += "" + buffer[i] + " ";
	}
	in.purgeAll();
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
