/* Programmer.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tools.pn;


import java.io.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.properties.*;
import bioera.storage.*;
import bioera.*;
import bioera.config.*;
import javax.swing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public abstract class Programmer extends Element {
	public int lineTransmitDelay = 6;

	private final static String propertiesDescriptions[][] = {
		{"lineTransmitDelay", "Line transmit delay", ""},
	};
	
	protected ScalarPipeDistributor outProgress;
	protected StringPipeDistributor outStatus, outSerial, outReceived;
	protected BufferedScalarPipe in;
	protected int inb[];
	protected String line, nLine;
	protected BufferedReader fin;
	protected int lineConfirmed = -1;
	protected int lineCounter, totalLines = 0;
	protected int integerRange = 0;
	protected int crcErrors = 0;
	protected boolean isPocket = false;
	protected long time = 0;

	protected static boolean debug = true;//bioera.Debugger.get("tools.pn.Programmer");
	protected static boolean debug1 = true;
/**
 * Element constructor comment.
 */
public Programmer() {
	super();
	setName("FirmwareUpgrade");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("IN");
	inb = in.getBuffer();

	outputs = new ScalarPipeDistributor[4];
	outputs[0] = outSerial = new StringPipeDistributor(this);
	outSerial.setName("OUT");
	outputs[1] = outStatus = new StringPipeDistributor(this);
	outStatus.setName("STATUS");
	outputs[2] = outProgress = new ScalarPipeDistributor(this);
	outProgress.setName("PROGRESS");
	outputs[3] = outReceived = new StringPipeDistributor(this);
	outReceived.setName("RECEIVED");
}
/**
 * Element constructor comment.
 */
public void checkConnectionTimeout() throws Exception {
	if (System.currentTimeMillis() - time > 1000) {
		if (System.currentTimeMillis() - time > 30000) {
			// Timeout elapsed
			if (debug)
				System.out.println("Upload failed: timeout elapsed");
			outStatus.writeLnString("Failed: timeout elapsed");

			// Stop processing
			Main.app.processor.setUpCounter(0);
		} else {
			outStatus.writeLnString("No connection");
			//System.out.println("no connection");
		}
	}
}
/**
 * Element constructor comment.
 */
protected final void finalizeOK() {
	if (debug)
		System.out.println("Done");
	outStatus.writeLnString("Done");
	outProgress.write(integerRange);

	// Make sure all buffers flush before processing stops
	Main.app.processor.setUpCounter(2);
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Pocket-Neurobics tool";
}
/**
 * Element constructor comment.
 */
public int getInputsCount() {
	return 1;
}
/**
 * Element constructor comment.
 */
public int getOutputsCount() {
	return 4;
}
/**
 * Element constructor comment.
 */
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
/**
 * Element constructor comment.
 */
public abstract Reader getReader() throws Exception;
/**
 * Element constructor comment.
 */
public void process() throws Exception {
	int n = in.available();
	if (n == 0) {
		checkConnectionTimeout();
		return;
	}

	if (debug) {
		System.out.print("Received (");
		int c;
		for (int i = 0; i < n; i++) {
			c = inb[i];
			if (c == '\n')
				System.out.print("\\n");
			else if (c == '\r')
				System.out.print("\\r");
			else
				System.out.print((char) c);
		}
		System.out.println(")");
	}

	if (outReceived.isConnected())
		writeToReceivedOutput();
		
	lineConfirmed = -1;	
	for (int i = 0; i < n; i++) {
		if (inb[i] == '*') {
			lineConfirmed = 1;
			break;
		} else if (inb[i] == '+')
			lineConfirmed = 0;		
	}

	if (lineConfirmed == -1) {
		// Response not arrived yet, check timeout
		checkConnectionTimeout();
		in.purgeAll();
		return;
	}

	time = System.currentTimeMillis();
		
	if (lineConfirmed == 1) {
		if (isPocket) {
			line = nLine;
			sendLine();
			nLine = fin.readLine();		
			if (nLine == null) {
				finalizeOK();
			}
		} else {
			line = fin.readLine();
			if (line == null) {
				finalizeOK();
			} else {
				sendLine();
			}
		}
	} else {
		// Send again
		//Thread.sleep(lineTransmitDelay);
		Thread.sleep(100);
		outSerial.writeString(line);
		if (debug)
			System.out.println("R(" + line + ")");
		outStatus.writeLnString("Retry (" + ++crcErrors + "/" + lineCounter + ")");
	}	
				
	in.purgeAll();
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	predecessorElement = getFirstElementConnectedToInput(0);
	if (predecessorElement == null) {
		throw new DesignException("Input not connected");
	} else if (!predecessorElement.isReinited()) {
		// Wait until the preceding element is inited
		return;
	}
	
	integerRange = getSignalParameters().getDigitalMax() / 2;	
		
	super.reinit();
}
/**
 * Element constructor comment.
 */
protected final void sendLine() throws Exception {
	if (debug)
		System.out.println("S("+line+")");
//	Thread.sleep(lineTransmitDelay);
	outSerial.writeString(line);
	outStatus.writeLnString("Uploading...");
	outProgress.write(integerRange * ++lineCounter / totalLines);
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
/**
 * Element constructor comment.
 */
public void start()  throws Exception {
	// Count all lines
	fin = new BufferedReader(getReader());
	totalLines = 0;
	lineCounter = 1;
	crcErrors = 0;
	while (fin.readLine() != null)
		totalLines ++;
	fin.close();

	// Reopen input stream 
	fin = new BufferedReader (getReader());

	// Read first line
	line = fin.readLine();

	// Send first line
	if (line != null)
	 	sendLine();
	
	if (isPocket) {
		nLine = fin.readLine();
	}

	// Update bars
	outProgress.write(0);
	outStatus.writeStringLn("");

	time = System.currentTimeMillis();
	
	if (debug)
		System.out.println("Started");	
}
/**
 * Element constructor comment.
 */
public void stop()  throws Exception {
	if (fin != null)
		fin.close();
}
/**
 * Element constructor comment.
 */
public void writeToReceivedOutput() throws Exception {
	outReceived.write(inb, 0, in.available());
}
}
