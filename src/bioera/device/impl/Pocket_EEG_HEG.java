/* Pocket_EEG_HEG.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.properties.*;

public final class Pocket_EEG_HEG extends Element {
	public int rate = 122;	// Default rate

	public ComboProperty mode = new ComboProperty(new String[]{"EEG", "HEG","Internal protocol", "Pendant"});

	private static final int EEG_MODE = 0;
	private static final int HEG_MODE = 1;
	private static final int INTERNAL = 2;
	private static final int PENDANT_MODE = 3;	

	private int type = PENDANT_MODE;
		
	private final static String propertiesDescriptions[][] = {
		{"batteryStatus","Battery","", "false"},
		{"mode","Mode",""},
		{"rate","Rate","", "false"},
	};	

	// Contains total packet length (4 consecutive samples) in 4-channel mode
	// If this device is in 2-channel mode, this value will be changed during synchronization
	private int PACKET_LENGTH = 20;  

	// Contains minimum number of bytes that contains at least 3 full packets
	// If this device is in 2-channel mode, this value will be changed during syncrhonization
	private int SYNCHR_LENGTH = 4 * PACKET_LENGTH;
		
	// Indicates whether device needs synchronization
	private boolean nsynchr = true;

	// Number of lost packets due to synchronization problem
	private int synchrLost = 0;

	// Number of successsful packets (not samples) processed	
	private int procNo = 0;

	// Input pipe	
	private BufferedScalarPipe in;

	// Output pipes		
	private ScalarPipeDistributor out0, out1, out2, out3, err;

	// Input buffer, signal rate, resolution bits, zelo level, channel number, channel shift
	private int inb[], rateBits = 0, resBits = 0, chN = 4, chS = 5;

	// Flags indicating if an output pipe is connected.
	private boolean conn0, conn1, conn2, conn3;

	private int statusBits = -1, hegch1, hegch2, alc1, alc2, alc3, alc4;

	private static final char S_RANGE = 'R';
	private static final char S_NOTCON = 'N';
	private static final char S_CONNECTED = 'C';
	private static final char S_SYNCHR = 'S';
	
		
	// Debugging flag, if set then additional information will be printed on console
	protected static boolean debug = bioera.Debugger.get("device.Pocket-EEG/HEG");	
public Pocket_EEG_HEG() {
	super();

	setName("Pocket-EEG/HEG");
	in = (BufferedScalarPipe) inputs[0];
	in.setName("I");
	inb = in.getBuffer();

	// 4 outputs
	outputs = new ScalarPipeDistributor[4];
	outputs[0] = out0 = new ScalarPipeDistributor(this);
	out0.setName("ch1");
	outputs[1] = out1 = new ScalarPipeDistributor(this);
	out1.setName("ch2");
	outputs[2] = out2 = new ScalarPipeDistributor(this);
	out2.setName("ch3");
	outputs[3] = out3 = new ScalarPipeDistributor(this);
	out3.setName("ch4");
}
public String getElementDescription() {
	return "Translates stream received from Pocket-EEG/HEG device into data channels";
}
public int getInputsCount() {
	return 1;
}
public int getOutputsCount() {
	return 4;
}
public Object [] getPropertyDescription(String name) {
	Object ret[] = searchPropertyDescription(name, propertiesDescriptions);
	if (ret == null)
		return super.getPropertyDescription(name);
	return ret;
}
public final void process() throws Exception {
	//System.out.println("available " + n);
	if (nsynchr) {
		// Wait for enough input samples to make synchronization on 3 full packets
		// SYNCHR_LENGTH is the size when 3 full packets should be found
		while (in.available() >= SYNCHR_LENGTH 
			&& (nsynchr = !synchronize())) {
			// Repeat it until synchronized or end of stream
		}

		if (nsynchr)
			return;
	}
	
	// Read one whole packet at a time
	while (in.available() >= PACKET_LENGTH) {
		// Validate synchronization of full packet
		if ((inb[0] >> 6) != 0 
			|| (inb[chS] >> 6) != 1 
			|| (inb[chS+chS] >> 6) != 2 
			|| (inb[chS+chS+chS] >> 6) != 3) {

			synchrLost++;

			// Write info about it to error node
			if (err != null)
				err.write(10 + synchrLost);

			// Print what is wrong
			if (debug) {
				for (int k = 0; k < PACKET_LENGTH; k++){
					System.out.print(" " + TOOLS.toBinaryString(inb[k]));
				}				
				System.out.println(" ** ok=" + procNo);
			}			

			System.out.println("Pocket-EEG/HEG synch lost: " + synchrLost + "/" + procNo);

			if (in.available() < SYNCHR_LENGTH) {
				// Clear one byte, so that this situation can't repeat itself
				in.purge(1);

				// Unset synchronization flag, 
				// Synchronization will be done next time
				nsynchr = true;
				return;
			} else {
				nsynchr = !synchronize();
				continue;
			}			
		}

		procNo++;			

		if (statusBits != (inb[chS+chS] & 15)) {
			statusBits = inb[chS+chS] & 15;
			if (err != null) {
				// Handle only 2 channels
				if (conn0 && (statusBits & 1) == 0)
					err.write(1);
				if (conn1 && (statusBits & 2) == 0)
					err.write(2);
				if ((statusBits & 3) != 0)
					err.write(0);
			}

			char c;
			if (conn0) {
				c = (statusBits & 1) == 0 ? S_RANGE : S_CONNECTED;
				setStatusChar(c, 1, c == S_RANGE ? 10 : 1);
			} else
				setStatusChar(S_NOTCON, 1, 0);
			if (conn1) {
				c = (statusBits & 2) == 0 ? S_RANGE : S_CONNECTED;
				setStatusChar(c, 2, c == S_RANGE ? 10 : 1);
			} else
				setStatusChar(S_NOTCON, 2, 0);
			if (conn2) {
				c = (statusBits & 4) == 0 ? S_RANGE : S_CONNECTED;
				setStatusChar(c, 3, c == S_RANGE ? 10 : 1);
			} else
				setStatusChar(S_NOTCON, 3, 0);
			if (conn3) {
				c = (statusBits & 8) == 0 ? S_RANGE : S_CONNECTED;
				setStatusChar(c, 4, c == S_RANGE ? 10 : 1);
			} else
				setStatusChar(S_NOTCON, 4, 0);
		}
		
		// Send data to connected channels
		switch (type) {
			case PENDANT_MODE:  // 2 channels, 12 bits
				int n;
				if (conn0) {
					for (int i = 0; i < 4; i++){
						n = (inb[i * chS + 1] << 4) + (inb[i * chS + 3] & 15);
						out0.write((n ^ 2048) - 2048);
					}
				}
				if (conn1) {
					for (int i = 0; i < 4; i++){
						n = (inb[i * chS + 2] << 4) + (inb[i * chS + 4] & 15);
						out1.write((n ^ 2048) - 2048);
					}
				}
				break;					
			case EEG_MODE:  // EEG & EMG
				if (conn0) {
					out0.write(((((inb[1] ^ 128) - 128)) * (64 - alc1)) / 16);
					alc1 = inb[chS] & 63;
					for (int i = 1; i < 4; i++){						
						out0.write(((((inb[i * chS + 1] ^ 128) - 128)) * (64 - alc1)) / 16);
					}
				}
				if (conn1) {
					out1.write(((((inb[2] ^ 128) - 128)) * (64 - alc2)) / 16);
					alc2 = inb[chS] & 63;	// ALC for ch1-2
					for (int i = 1; i < 4; i++){
						out1.write(((((inb[2 + i * chS] ^ 128) - 128)) * (64 - alc2)) / 16);
					}
				}
				
				if (chN == 2)
					break;
					
				if (conn2) {
					out2.write(((((inb[3] ^ 128) - 128)) * (64 - alc3)) / 16);
					alc3 = inb[chS+chS+chS] & 63; // ALC for ch3-4
					for (int i = 1; i < 4; i++){
						out2.write(((((inb[i * chS + 3] ^ 128) - 128)) * (64 - alc3)) / 16);
					}
				}
				if (conn3) {
					out3.write(((((inb[4] ^ 128) - 128)) * (64 - alc4)) / 16);
					alc4 = inb[chS+chS+chS] & 63; // ALC for ch3-4
					for (int i = 1; i < 4; i++){
						out3.write(((((inb[i * chS + 4] ^ 128) - 128)) * (64 - alc4)) / 16);
					}
				}
				break;

			case HEG_MODE:
				if (conn0) {
					out0.write(((inb[1] ^ 128) << 3) + hegch1 - 1024);
					hegch1 = inb[chS] & 7;
					out0.write(((inb[1 + chS] ^ 128) << 3) + hegch1 - 1024);
					out0.write(((inb[1 + chS + chS] ^ 128) << 3) + hegch1 - 1024);
					out0.write(((inb[1 + chS + chS + chS] ^ 128) << 3) + hegch1 - 1024);
				}
				if (conn1) {
					out1.write(((inb[2] ^ 128) << 3) + hegch2 - 1024);
					hegch2 = (inb[chS] >> 3) & 7;
					out1.write(((inb[2 + chS] ^ 128) << 3) + hegch2 - 1024);
					out1.write(((inb[2 + chS + chS] ^ 128) << 3) + hegch2 - 1024);
					out1.write(((inb[2 + chS + chS + chS] ^ 128) << 3) + hegch2 - 1024);
				}
				
				if (chN == 2)
					break;
					
				if (conn2) {
					out2.write(((((inb[3] ^ 128) - 128)) * (64 - alc3)) / 8);
					alc3 = inb[chS+chS+chS] & 63; // ALC for ch3-4
					for (int i = 1; i < 4; i++){
						out2.write(((((inb[i * chS + 3] ^ 128) - 128)) * (64 - alc3)) / 8);
					}
				}
				if (conn3) {
					out3.write(((((inb[4] ^ 128) - 128)) * (64 - alc4)) / 8);
					alc4 = inb[chS+chS+chS] & 63; // ALC for ch3-4
					for (int i = 1; i < 4; i++){
						out3.write(((((inb[i * chS + 4] ^ 128) - 128)) * (64 - alc4)) / 8);
					}
				}

				
				break;
			case INTERNAL:
				if (conn0) {
					for (int i = 0; i < 4; i++){
						out0.write((inb[i * chS + 1] ^ 128) - 128);
					}
				}
				if (conn1) {
					for (int i = 0; i < 4; i++){
						out1.write((inb[i * chS + 2] ^ 128) - 128);
					}
				}
				
				if (chN == 2)
					break;
					
				if (conn2) {
					for (int i = 0; i < 4; i++){
						out2.write((inb[i * chS + 3] ^ 128) - 128);
					}
				}
				if (conn3) {
					for (int i = 0; i < 4; i++){
						out3.write((inb[i * chS + 4] ^ 128) - 128);
					}
				}
				break;
		}

		in.purge(PACKET_LENGTH);
	}
}
private void readDeviceSettings() throws Exception {
	if (rateBits == -1) {
		// Initialize new rate
		// Check if all 3 packets have the same value
		int v = inb[0] & 7;
		//System.out.println("rate bits=" + Integer.toBinaryString(inb[0]));		
		if (v == (inb[PACKET_LENGTH] & 7) || v == (inb[PACKET_LENGTH << 1] & 7)) {
			// Calculate bits from the rate set previously
			int rB = -2;
			if (rate == 122) {
				rB = 0;
			} else {
				for (int i = 0; i < 7; i++){
					if (rate == (128 << i)) {
						rB = i + 1;
						break;
					}
				}
			}			
			
			if (v != rB) {
				rateBits = v;
				if (v == 0)
					rate = 122;
				else
					rate = 128 << (rateBits - 1);

				System.out.println("Pocket-EEG/HEG: rate set to " + rate);
					
				// Reinitialize all elements
				setOutputSignalRate(rate);
				bioera.Main.app.processor.reInitializeAll();
			}
		}
	}
}
public void reinit() throws Exception {
	if (mode.getSelectedIndex() == -1)
		mode.setSelectedIndex(type);
	else
		type = mode.getSelectedIndex();

	conn0 = out0.isConnected();
	conn1 = out1.isConnected();
	conn2 = out2.isConnected();
	conn3 = out3.isConnected();

	// We need to limit max value to prevent too high memory allocation of buffers
	// When this (memory allocation) will be eventually improved, then this limitation here will be taken out.
	if (rate > 1024)
		rate = 1024;
	if (rate < 0)
		rate = 122;
		
	rateBits = -1;
			
	setOutputSignalRate(rate);  // default rate
	getSignalParameters().setPhysicalUnit("uV");

	switch (mode.getSelectedIndex()) {
		case EEG_MODE:
			setOutputDigitalRange(1024);
			setOutputPhysicalRange(500);
			setOutputResolutionBits(10);
			//out0.setName("eeg1");
			//out1.setName("eeg2");
			//out2.setName("eeg3");
			//out3.setName("eeg4");
			break;
		case PENDANT_MODE:
			setOutputDigitalRange(1 << 12);
			setOutputPhysicalRange(500);
			setOutputResolutionBits(12);
			break;
		case HEG_MODE:
//			zL = 2048 / 2;
			setOutputDigitalRange(2048);
			setOutputPhysicalRange(256);
			setOutputResolutionBits(11);
			//out0.setName("heg1");
			//out1.setName("heg2");
			//out2.setName("n/u");
			//out3.setName("n/u");
			break;
		case INTERNAL:
//			zL = 0;
			setOutputDigitalRange(256);
			setOutputPhysicalRange(256);
			setOutputResolutionBits(8);
			//out0.setName("out1");
			//out1.setName("out2");
			//out2.setName("out3");
			//out3.setName("out4");
			break;
	}

	if (mode.getSelectedIndex() == PENDANT_MODE) {
		err = out3;
	} else
		err = null;
		
	super.reinit();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
private final boolean synchronize() throws Exception {
	// First try to synchronize 4-channel frames
	if (synchronize4()) {
		setStatusChar(' ', 0, 0);
		return true;
	}

	// Now try to synchronize 2-channel frames
	if (synchronize2()) {
		setStatusChar(' ', 0, 0);
		return true;
	}


	//String msg = "Pocket-EEG/HEG synchronization bytes not found withinin " + SYNCHR_LENGTH + " bytes: ";
	//for (i = 0; i < Math.min(SYNCHR_LENGTH, PACKET_LENGTH); i++){
		//String s = Integer.toBinaryString(inb[i]);
		//while (s.length() < 8)
			//s = "0" + s;
		//msg += s + "\n";
		
//		msg += "" + inb[i] + " ";
	//}

	synchrLost ++;
	in.purge(PACKET_LENGTH);

	setStatusChar('S', 0, 10);

	if (err != null)
		err.write(10 + synchrLost);
	
	// Check if the synchronization has any chance for success
	// If more then 10% of packets fails, then it is not acceptable
	if (synchrLost > 100 && procNo / synchrLost < 10) {
		synchrLost = 0;
		procNo = 0;
		//setActive(false);
		System.out.println("Synchronization failure rate above 10%. Check connections.");
		//setDesignErrorMessage("Synchronization failure above 10%");
	}
	
	return false;
}
private final boolean synchronize2() throws Exception {
	// Check syncchronization
	int i = -1, m = 128 + 64;

	// Check 2-channel synchronization
	start: while (++i < SYNCHR_LENGTH) {
		//System.out.println("b"+i+"=" + TOOLS.toBinaryString(inb[i]));
		if ((inb[i] >> 6) == 0) {
			// Found first byte, check against all remaining bytes
			int k = i + 3, c = 1;
			while (k < SYNCHR_LENGTH) {
				//System.out.println("k"+k+"=" + TOOLS.toBinaryString(inb[k]));
				if ((inb[k] >> 6) != c)
					// Not synchronized
					continue start;
				c = (c + 1) % 4;
				k += 3;
			}

			// Ok, found synchronization sequence.
			// Make sure it is not a bad luck
			if (i < PACKET_LENGTH / 2) {
				// OK - clean preceeding bytes and return
				in.purge(i);
				//System.out.println("ok");
				chN = 2;
				chS = 3;
				PACKET_LENGTH = 4 * chS;				
				SYNCHR_LENGTH = PACKET_LENGTH * 4;
				readDeviceSettings();
				return true;
			} else {
				//System.out.println("not ok: " + i);								
				break;
			}
		}
	}
	
	return false;
}
private final boolean synchronize4() throws Exception {
	// Check syncchronization
	int i = -1, m = 128 + 64;

	// Check 4-channel synchronization
	start: while (++i < SYNCHR_LENGTH) {
		//System.out.println("b"+i+"=" + TOOLS.toBinaryString(inb[i]));
		if ((inb[i] >> 6) == 0) {
			// Found first byte, check against all remaining bytes
			int k = i + 5, c = 1;
			while (k < SYNCHR_LENGTH) {
				//System.out.println("k"+k+"=" + TOOLS.toBinaryString(inb[k]));
				if ((inb[k] >> 6) != c)
					// Not synchronized
					continue start;
				c = (c + 1) % 4;
				k += 5;
			}

			// Ok, found synchronization sequence.
			// Make sure it is not a bad luck
			if (i < PACKET_LENGTH) {
				// OK - clean preceeding bytes and return
				in.purge(i);
				//System.out.println("ok");
				chN = 4;
				chS = 5;
				PACKET_LENGTH = 4 * chS;
				SYNCHR_LENGTH = PACKET_LENGTH * 4;
				readDeviceSettings();
				return true;
			} else {
				//System.out.println("not ok: " + i);								
				break;
			}
		}
	}

	return false;
}
}
