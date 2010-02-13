/* ModEEGBackComm.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.device;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import bioera.processing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ModEEGBackComm {
	public final static int RECV_PORT_SET 					= 1;
	public final static int RECV_PORTD_BIT_SET 				= 2;
	public final static int RECV_PORTD_BIT_BRIGHT_ONTIME 	= 3;
	public final static int RECV_PORTD_BIT_BRIGHT_PERIOD 	= 4;
	public final static int RECV_CHANNEL_SET				= 5;
	public final static int RECV_GET_VINFO 					= 6;
	public final static int RECV_SET_VINFO 					= 7;

	public final static int VINFO_PROTOCOL_NUMBER 			= 1;
	public final static int VINFO_CHANNELS_MATRIX			= 2;
	public final static int VINFO_BAUD_RATE 				= 3;	
	public final static int VINFO_PORTD 					= 4;
	public final static int VINFO_PORTB 					= 5;	
		
	public final static int CMD = 128;
	public final static int MAX_DATA_VALUE = 127;
	
	private OutputStream outputStream;
	private InputStream inputStream;

	protected static boolean debug = bioera.Debugger.get("modeeg.backcomm");
/**
 * Tools constructor comment.
 */
public ModEEGBackComm(OutputStream o) {
	this(o, null);
}
	//System.getProperty("bioera.device.modeeg.backcomm") != null;	
/**
 * Tools constructor comment.
 */
public ModEEGBackComm(OutputStream o, InputStream in) {
	outputStream = o;
	inputStream = in;
}
/**
 * Tools constructor comment.
 */
public int cmd21GetVInfo(int type, boolean readResult) {
	if (readResult && inputStream != null) {
		flushInputStream();
	}

	// Send to command to serial port	
	sendCmd(CMD + RECV_GET_VINFO, type, 0);

	if (readResult && inputStream != null) {
		// Receive result from serial port
		return read21RequestedVInfo(type);
	} else { 	
		return -1;
	}
}
/**
 * Tools constructor comment.
 */
public int cmdGetMatrix() {
	int ret = cmd21GetVInfo(VINFO_CHANNELS_MATRIX, true);
	return ret;
}
/**
 * Tools constructor comment.
 */
public int cmdGetProtocol() {
	// First try recognize protocol 21
	int ret = cmd21GetVInfo(VINFO_PROTOCOL_NUMBER, true);

	if (ret < 0 && inputStream != null) {
		if (debug)
			System.out.println("Protocol 21 was not recognized ("+ret+")");

		// Now try recognize protocol 2	
		ret = readProtocol2();
	}
		
	return ret;
}
/**
 *	Sets port B state, bits 0-6
 *  Value range: 0 - 63
 */
public void cmdPortBSet(int v) {
	if (v < 0 || v > 63)
		throw new RuntimeException("Wrong value " + v);
	sendCmd(CMD + RECV_PORT_SET, 'B', v);
}
/**
 * Tools constructor comment.
 */
public void cmdPortDBitBrightOnTime(int bit, int v) {
	sendCmd(CMD + RECV_PORTD_BIT_BRIGHT_ONTIME, bit,v);
}
/**
 * Tools constructor comment.
 */
public void cmdPortDBitBrightPeriod(int bit, int v) {
	sendCmd(CMD + RECV_PORTD_BIT_BRIGHT_PERIOD, bit, v);	
}
/**
 * Tools constructor comment.
 */
public void cmdPortDBitSet(int bit, boolean state) {
	sendCmd(CMD + RECV_PORTD_BIT_SET, bit, state ? 1 : 0);	
}
/**
 *	Sets port D state, bits 2-7
 *  Value range: 0 - 63
 */
public void cmdPortDSet(int v) {
	if (v < 0 || v > 255)
		throw new RuntimeException("Wrong value " + v);

	// Value is shifted here 2 positions, since the lowest 2 bits are reserved and the value must be up to 7 bit long,
	// it is reshifted back in microcontroler
	sendCmd(CMD + RECV_PORT_SET, 'D', v >> 2);
}
/**
 * Tools constructor comment.
 */
public void cmdSetBaudRate21(int v) {
	// Not implemented
}
/**
 * Tools constructor comment.
 */
public void cmdSetChannelMatrix21(int v) {
	cmdSetVInfo(VINFO_CHANNELS_MATRIX, v);
}
/**
 * Tools constructor comment.
 */
public void cmdSetProtocol(int nb) {
	cmdSetVInfo(VINFO_PROTOCOL_NUMBER, nb);
}
/**
 * Tools constructor comment.
 */
private void cmdSetVInfo(int type, int value) {
	sendCmd(CMD + RECV_SET_VINFO, type, value);
}
/**
 * Although there is flush() method in InputStream class, 
 * it is safer (more portable) to just read the bytes 
 */
public void flushInputStream() {
	if (inputStream != null) {
		try {
			int n = inputStream.available();
			while (n-- > 0)
				inputStream.read();
		} catch (Exception e) {
			System.out.println("Could not flush input stream: " + e);
		}
	}	
}
/**
 * Tools constructor comment.
 */
private int read21RequestedVInfo(int type) {
	if (inputStream == null)
		return -11;

	try {
		ProtocolV21InputStream in = new ProtocolV21InputStream(inputStream);

		long time = System.currentTimeMillis();
		int counter = 0;

		// Read up to first 200 packets (1 second) and search for the requested data
		while (true){
			// Make sure that something has arrived to the port, 
			// we do not want to stuck here
			while (inputStream.available() > 15 && counter < 200){
				counter++;
				try {
					in.readPacket();
				} catch (Exception e) {
					// Packet not recognied properly
					// It is possible that protocol has been just changed,
					// So wait until enough packets arrive until generate error
					if (counter > 100) {
						if (debug)
							System.out.println("SerialPort VInfo: Format of the data from serial port was not recognized: " + e.getMessage());
						return -12;
					}
					
					continue;
				}
				
				if (in.getRequestedInfoType() != -1) {
					// Ok, something arrived, see if that is what we were looking for
					if (in.getRequestedInfoType() == type) {
						// OK, here it is
						if (debug)
							System.out.println("Requested vinfo arrived " + in.getRequestedInfoType() + "=" + in.getRequestedInfoValue() + " (" + counter + "' packet)");
						return in.getRequestedInfoValue();
					} else {
						if (debug)
							System.out.println("SerialPort VInfo: Unexpected, arrived data is different type then requested '" + in.getRequestedInfoType() + "'='" + in.getRequestedInfoValue() + "'");
					}
				}
			}

			// If we are here it means that 
			// either port is not receiving anything
			// or the packet not yet arrived

			// Check for timeout
			if (System.currentTimeMillis() - time > 1000) {
				if (counter == 0) {
					if (debug)
						System.out.println("SerialPort VInfo: no data received during 1 second");
					return -13;
				} else {
					if (debug)
						System.out.println("SerialPort VInfo: requested data type (" + type + ") not found within " + counter + " packets");
					return -14;
				}
			}

			Thread.sleep(10);
		}
	} catch (Exception e) {
		System.out.println("SerialPort VInfo: Unexpected problem: " + e);
		return -15;
	}
}
/**
 * Tools constructor comment.
 */
private int readProtocol2() {
	if (inputStream == null)
		return -11;

	try {
		if (debug)
			System.out.println("Checking protocol 2");
		
		ElecGuruInputStream in = new ElecGuruInputStream(inputStream);

		long time = System.currentTimeMillis();
		int counter = 0;

		// Read up to first 200 packets (1 second) and search for the requested data
		while (true){
			// Make sure that something has arrived to the port, 
			// we do not want to stuck here
			while (inputStream.available() >= 17 && counter < 200){
				counter++;
				try {
					in.readPacket();
				} catch (Exception e) {
					// Packet not recognized properly
					// It is possible that protocol has been just changed,
					// So wait until enough packets arrive until generate error
					if (counter > 100) {
						if (debug)
							System.out.println("SerialPort V2: Format of the data from serial port was not recognized");
						return -12;
					}
					
					continue;
				}

				// If we are here it means parsing was ok, this is the protocol
				if (debug)
					System.out.println("SerialPort V2: Protocol 2 found within " + counter + " packets");
					
				return 2;
			}

			// If we are here it means that 
			// either port is not receiving anything
			// or the packet not yet arrived

			// Check for timeout
			if (System.currentTimeMillis() - time > 1000) {
				if (counter == 0) {
					if (debug)
						System.out.println("SerialPort V2: no data received during 1 second");
					return -13;
				} else {
					if (debug)
						System.out.println("SerialPort V2: protocol 2 not recognized within " + counter + " packets");
					return -14;
				}
			}

			Thread.sleep(10);
		}
	} catch (Exception e) {
		System.out.println("SerialPort V2: Unexpected problem: " + e);
		return -15;
	}
}
/**
 *	Sets port D state, bits 3-8
 *  Value range: 0 - 63
 */
private void sendCmd(int cmd, int selector, int value) {
	if (selector < 0 || selector > MAX_DATA_VALUE) {
		throw new RuntimeException("Range of selector exceeded: " + selector);
	}
	if (value < 0 || value > MAX_DATA_VALUE) {
		throw new RuntimeException("Range of value exceeded: " + value);
	}

	try {
		if (debug)
			System.out.println("ModEEG: sent command: " + cmd + " (" + selector + ", " + value + ")");
		if (inputStream != null) {
			// Clear input
			int n = inputStream.available();
			while (n-- > 0)
				inputStream.read();
		}

		outputStream.write(cmd);
		outputStream.write(selector);
		outputStream.write(value);
		outputStream.flush();		
	} catch (Exception e) {
		// If there is an error, wait and repeat once again
		try {
			Thread.sleep(500);
			outputStream.write(cmd);
			outputStream.write(selector);
			outputStream.write(value);
			outputStream.flush();					
		} catch (Exception e1) {
			System.out.println("ModEEG back comm ERROR: " + e1);
		}		
	}

}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
}
