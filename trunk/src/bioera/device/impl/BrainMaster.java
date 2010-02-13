/* BrainMaster.java v 1.0.9   11/6/04 7:15 PM
 *
 * BioEra - visual designer for biofeedback (http://www.bioera.net)
 *
 * Copyright (c) 2003-2004 Jarek Foltynski (http://www.foltynski.info)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package bioera.device.impl;

import bioera.processing.*;
import bioera.fft.*;
import bioera.properties.*;
import java.io.*;
import bioera.processing.impl.SerialPort;

/**
 * Creation date: (6/2/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public final class BrainMaster extends Element {
	public ComboProperty mode = new ComboProperty(new String[]{"1 channel", "2 channels"});
	private final static String propertiesDescriptions[][] = {
		{"mode", "Mode", ""},
		{"initializeDevice", "Initialize device", ""}
	};

	public boolean initializeDevice = false;

	private int type = 1;

	private BufferedScalarPipe in;
	private ScalarPipeDistributor out, out1;
	private int inb[], counter = 0, synchr = 0, processed = 0;

	protected static boolean debug = true;


public BrainMaster() {
	super();

	setName("BM");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	in.setBufferSize(2048);

	inb = in.getBuffer();

	// 2 channels
	out = (ScalarPipeDistributor) outputs[0];
	out.setName("ch1");
	out1 = (ScalarPipeDistributor) outputs[1];
	out1.setName("ch2");
}
public String getElementDescription() {
	return "Translates stream from BrainMaster format into data channels";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return 2;
}
/**
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
private boolean inCompare(InputStream in, String s) throws Exception {
	if (in.available() < s.length()) {
		// Wait more
		Thread.sleep(100);
	}

	if (in.available() < s.length()) {
		// If still not enough bytes available, consider it failed
		return false;
	}

	for (int i = 0; i < s.length(); i++){
		if (s.charAt(i) != in.read())
			if (s.charAt(i) != '?')
				return false;
	}

	return true;
}
private String initializeDevice() throws Exception {
	SerialPort sp = (SerialPort) predecessorElement;
	OutputStream outputStream = sp.getOutputStream();
	InputStream in = sp.getInputStream();
	Writer out = new OutputStreamWriter(outputStream);

    // first sequence is to break

    if (debug)
    	System.out.println("BM: Flushing buffers...");

    out.write(0x16);
    out.write(0x04);
    out.write(0x03);
    out.write('\r');

    Thread.sleep(100);

    out.flush();

    // second sequence to check control

    if (debug)
    	System.out.println("BM: Resetting BrainMaster...");

    out.write(0x16);
    out.write(0x04);
    out.write(0x03);
    out.write('\r');

    Thread.sleep(100);

	// Read input string
    if(in.read() != 0x16 || in.read() != 0x04 || in.read() != 0x03
		|| !inCompare(in, "\r\n\r\n*What?\r\n*>")) {
		return "Brainmaster not responding (1)";
    }

    // program for two channels by writing the value $0C in memory cell $B607

    if (debug)
    	System.out.println("BM: Programming for two channel mode...");

    out.write("mm b607\r");

    if(!inCompare(in, "mm b607\r\n\r\n*B607 ??")) {
	   return "Brainmaster not responding (2)";
    }

    out.write("0C\r");

    if(!inCompare(in, "0C\r\n\r\n*>")){
		return "Brainmaster not responding (3)";
    }

    // send synch byte, and start ADC running

    if (debug)
    	System.out.println("BM: Programming protocol ...");

    out.write("mm b608\r");

    if(!inCompare(in, "mm b608\r\n\r\n*B608 ?? ")) {
	    return "Brainmaster not responding (4)";
    }

    out.write("20\r");

    if(!inCompare(in, "20\r\n\r\n*>")) {
	    return "Brainmaster not responding (5)";
    }

    // 5 input bits, 3 synch bits

    if (debug)
    	System.out.println("BM: Programming synch byte format...");

    out.write("mm b609\r");

    if(!inCompare(in, "mm b609\r\n\r\n*B609 ?? ")) {
       return "Brainmaster not responding (6)";
    }

    out.write("3\r");

    if(!inCompare(in, "3\r\n\r\n*>"))  {
	    return "Brainmaster not responding (7)";
    }

    // start with data

    if (debug)
    	System.out.println("BM: Running OK");

    out.write("call b603\r");

    // OK
    return null;
}
public final void process() throws Exception {
    int n = in.available();
    if (n < 3)
        return;

    switch (type) {
        case 1 : // BM2
            if (counter == 0) {
	            if (n > 11) {
	            	if (!synchronize2())
	            		return;
	            } else {
	            	// Not enough bytes to synchronize, exit and wait
	            	return;
	            }
            }
            int i;
            for (i = 0; i < n - 3; i+=3) {
                if ((inb[i] >> 5) != counter) {
	                synchr++;
	                if (synchr > 100 && processed < 10) {
		                // Too many synchronization errors
		                System.out.println("BrainMaster synchronization errors");
		                synchr = 0;
		                processed = 0;
		                in.purgeAll();
		                return;
	                }

	                if (!synchronize2())
	                	return;
                }

                if (++counter > 7)
                    counter = 1;

                out.write(inb[i + 1] - 128);
                out1.write(inb[i + 2] - 128);
            }
            in.purge(i);
            break;
        case 0 : // BM1
            // One channel only, copy all
            for (i = 0; i < n; i++){
            	out.write(inb[i] - 128);
            }
            in.purgeAll();
            break;
    }
}
public void reinit() throws Exception {
	if (mode.getSelectedIndex() == -1)
		mode.setSelectedIndex(type);
	else
		type = mode.getSelectedIndex();

	// We need to wait until SerialPort (connected to input) is initialized
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		throw new bioera.graph.designer.NotConnectedException();
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}

	if (initializeDevice && (predecessorElement instanceof SerialPort)) {
		((SerialPort)predecessorElement).initPort();
		String errMsg = initializeDevice();
		((SerialPort)predecessorElement).close();
		if (errMsg != null) {
		    if (debug)
				System.out.println("Brainmaster initialization failed: " + errMsg);
			throw new bioera.graph.designer.DesignException(errMsg);
		}
	}

	setOutputDigitalRange(256);
	setOutputPhysicalRange(200);
	setOutputResolutionBits(8);
	setOutputSignalRate(122);

	super.reinit();

	if (debug)
		System.out.println("Brainmaster initialized OK");
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
public void start() throws Exception {
	counter = 0;
}
private final boolean synchronize2() throws Exception {
	int c, n = in.available();

	// One of the first 3 bytes is a counter
	// Find it and verify
	start: for (int i = 0; i < 3; i++){
		c = inb[0] >> 5;
		if (++c > 7)
			c = 1;
		for (int k = 3; k < n - 3; k+=3){
			if (c != (inb[k] >> 5)) {
				// Synchr failed
				// continue from the next byte
				in.purge(1);
				continue start;
			}
			if (++c > 7)
				c = 1;
		}

		// OK, found and verified the counter
		counter = (inb[0] >> 5);
		return true;
	}

	System.out.print("BrainMaster synchronization failed (");
	for (int i = 0; i < Math.min(n, 10); i++){
		if (i > 0)
			System.out.print(",");
		System.out.print("" + inb[i]);
	}
	System.out.println(")");

	counter = 0;
	in.purgeAll();

	return false;
}
}
