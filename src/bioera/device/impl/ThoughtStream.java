/*
ThoughtStream.java v1 3/3/05
2005 Gary Bense sales@mindmodulations.com
*/


package bioera.device.impl;

import bioera.processing.*;
import bioera.fft.*;
import java.io.*;
import bioera.processing.impl.SerialPort;

public final class ThoughtStream extends Element {

	public final static int HEADER_1 = 0xaa; // decimal 170
	public final static int HEADER_2 = 0xaa; // decimal 170
	public final static int VERSION = 0x01;

	private int packetCounter = -1;
	private int packetsLost = 0;
	private int processedPackets = 0;

        private int type = 1;

	private BufferedScalarPipe in;
	private ScalarPipeDistributor out, out1;
	private int buffer[];

	protected static boolean debug = true;

public ThoughtStream() {
	super();

	setName("ThoughtStream");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	in.setBufferSize(64);
	buffer = in.getBuffer();
        out = (ScalarPipeDistributor) outputs[0];
        out.setName("GSR OUT");
    }

public String getElementDescription() {
	return "Translates ThoughtStream Galvonic Skin Response Data into BioEra";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return 1;
}


public final void process() throws Exception {
	int n = in.available();
	if (n < 12)
		return;

	if (packetCounter == -1) {
		searchPacketBegin();
	}

	while (in.available() >= 18) {
		if (buffer[0] != HEADER_1
			|| buffer[1] != HEADER_2
			|| buffer[2] != VERSION
			) {
			packetsLost++;

			if (debug=true) {
				if (buffer[0] != HEADER_1) System.out.println("ThoughtStream: header1 " + buffer[0]);
				if (buffer[1] != HEADER_2) System.out.println("ThoughtStream: header2 " + buffer[1]);
				if (buffer[2] != VERSION) System.out.println("ThoughtStream: version " + buffer[2]);
				if (buffer[3] != packetCounter) System.out.println("ThoughtStream: counter should be " + packetCounter);
				for (int k = 0; k < 9; k++){
					System.out.print(" " + buffer[k]);
				}
				System.out.println(" ** ok=" + processedPackets);
			}

                        System.out.println("Total Lost ThoughtStream Frames: " + packetsLost);
			searchPacketBegin();

			//continue;
		}

		processedPackets++;

		for (int i = 0; i < 1; i++){
			if (out.isConnected()) {
                                n = buffer[4];
                                out.write(n);
			}
		}

		packetCounter = (packetCounter + 1);
		in.purge(9);
	}
}
public void reinit() throws Exception {
	packetCounter = -1;
	super.reinit();
}
private final void searchPacketBegin() throws Exception {

	for (int i = 0; i < 9; i++){
		if (buffer[i] == HEADER_1 && buffer[i + 1] == HEADER_2 && buffer[i + 2] == VERSION) {
			in.purge(i);
                        packetCounter = packetCounter+1;
			return;
		}
	}

	String msg = "ThoughtStream header not found in " + 9 + " bytes: ";
	for (int i = 0; i < 10; i++){
		msg += "" + buffer[i] + " ";
	}
	in.purgeAll();
}

public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start() throws Exception {
	packetCounter = -1;
	in.purgeAll();
}

    protected void jbInit() throws Exception {
    }
}
