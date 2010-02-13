/* TS2back.java v 1.0.9   1/25/06 7:15 PM
 *
 *
 */

package bioera.device.impl;

import bioera.processing.*;
import bioera.fft.*;

public final class TS2back extends Element {
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
        public final static int HDRok = 0x10; // CMD received OK

        private int rcvok = -1;
        private int packetCounter = -1;
        private int packetsLost = 0;
        private int processedPackets = 0;

        private BufferedScalarPipe in;
        private ScalarPipeDistributor out[];
        private int buffer[];

        protected static boolean debug = bioera.Debugger.get("device.ts2back");


public TS2back() {
        super();
        setName("TS2back");
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
/**
 */
public Object [] getPropertyDescription(String name) {
        Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
        if (ret == null)
                return super.getPropertyDescription(name);
        return ret;
}

public final void process() throws Exception {
        int n = in.available();
        if (packetCounter == -1) {
                sendStartCMD();
                searchPacketBegin();
        }



        while (in.available() >= 15)
        {
            if (buffer[0] != HEADER_1 || buffer[1] != HEADER_2_16ds)
            {
                packetsLost++;
                System.out.println("Total frames lost in ts2back: " + packetsLost);
                searchPacketBegin();
                continue;
            }
            processedPackets++;
            for (int i = 0; i < 4; i++)
            {
                if (out[i].isConnected())
                {
                    n = (buffer[2 + (i<<1)] << 8) + buffer[3 + (i<<1)];
                    out[i].write(n);
                }
            }
                packetCounter = (packetCounter + 1) % 256;
                in.purge(12);
        }
}
public void reinit() throws Exception {
        packetCounter = -1;
        super.reinit();
}

private final void sendStartCMD() throws Exception {
    if (rcvok == -1) {
        if (out[4].isConnected())
        {
            out[4].write(HEADER_1);
            out[4].write(HEADER_2_Cmd);
            out[4].write(0x06);
            out[4].write(HDRCmdStart);
            out[4].write(0x01);
            out[4].write(0x12);
        }
        iscmdReceived();
    }
    return;
}


private final void iscmdReceived() throws Exception {
    for (int i = 0; i < 40; i++)
    {
         if ((buffer[i] == HEADER_1) && (buffer[i + 1] == HEADER_2_16ds) && (buffer[i + 3] == HDRok))
         {
             in.purge(i);
             rcvok = 1;
             return ;
         }
     }

}


private final void searchPacketBegin() throws Exception {

        for (int i = 0; i < 20; i++)
        {
                if ((buffer[i] == HEADER_1) && (buffer[i + 1] == HEADER_2_16ds))
                {
                    in.purge(i);
                    return;
                }
        }
        in.purgeAll();



/*        String msg = "TS2back header not found in " + 14 + " bytes: ";
        for (int i = 0; i < 15; i++){
                msg += "" + buffer[i] + " ";
        }
*/
}

public static void setDebug(boolean newValue) {
        debug = newValue;
}
public void start() throws Exception {
        packetCounter = -1;
        in.purgeAll();
}

}
