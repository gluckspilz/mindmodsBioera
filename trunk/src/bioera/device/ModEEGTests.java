/* ModEEGTests.java v 1.0.9   11/6/04 7:15 PM
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
import javax.comm.*;
import java.text.DateFormat;
import bioera.processing.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class ModEEGTests implements Runnable {
	SerialPort serialPort;
	int readCounter;
/**
 * Tools constructor comment.
 */
public void brightnessTest() throws Exception {
	OutputStream out = serialPort.getOutputStream();

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();

	// Set brightness
	out.write(128+3);
	out.write(1);
	out.write(128+4);
	out.write(4);

	// Turn on
	out.write(130);
	out.write(1);

		
	boolean b = true;
	while (true) {
		if (System.in.available() > 0) {
			int k = System.in.read();
			if (Character.isDigit((char) k)) {
				int thresh = k - '0';
				out.write(128+3);
				out.write(thresh);
				System.out.println("Value is " + (char) ('0' + thresh));					
			}
		}
		
		Thread.sleep(100);
	}
}
/**
 * Tools constructor comment.
 */
public void freqTest() throws Exception {
	OutputStream out = serialPort.getOutputStream();

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();
		
	boolean b = true;
	int v = 100;
	int counter = 0;
	int thresh = 10;
	//5760 / 2;
	long time;
	while (thresh != 0) {
		if (System.in.available() > 0) {
			int k = System.in.read();
			if (Character.isDigit((char) k)) {
				thresh = k - '0';
				System.out.println("Theshold is " + (char) ('0' + thresh));					
			}
		}

		counter = readCounter;
		time = System.currentTimeMillis();
		for (int i = 0; i < v; i++){
			if (b) {
				//out.write(129);
				//out.write(32);
				out.write(130);
				out.write(1);
			} else {
				out.write(130);
				out.write(0);
			}
			b = !b;
			Thread.sleep(2 * thresh);
		}
		
		long now = System.currentTimeMillis();
		System.out.println("" + v + " changes in " + (now - time) + "ms  sleep=" + (2 * thresh));
		System.out.println("freq=" + (1000 * (v / 2) / (now - time)) + "hz");
		System.out.println("read " + (readCounter - counter) + " bytes");
		System.out.println();
	}
}
/**
 * Tools constructor comment.
 */
public  void init() throws Exception {
	String device = "COM1";
	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	CommPortIdentifier portId = null;
    
	while (portList.hasMoreElements()) {
		portId = (CommPortIdentifier) portList.nextElement();
		System.out.println("Port: " + portId.getName());
		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			if (portId.getName().equals(device)) {
				// Found the port
				break;
			}
		}
		portId = null;
	}

	if (portId == null)
		throw new Exception("Not found port '" + device + "'");

	try {
		serialPort = (SerialPort) portId.open("SerialApp", 2000);
	} catch (PortInUseException e) {
		throw new IOException("Port '" + device + "' is already in use");
	}

	serialPort.notifyOnDataAvailable(false);

	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	
	try {		
		serialPort.setSerialPortParams(57600, dataBits, stopBits, parity);
	} catch (UnsupportedCommOperationException e) {
		throw new IOException("Unexpected error: " + e);
	}
}
/**
 * Tools constructor comment.
 */
public static void main(String args[]) throws Exception {
	long time = 0;
	try {
		System.out.println("started");
		ModEEGTests t = new ModEEGTests();
		t.init();
//		t.version21VerifyMatrix();
//		t.readHTest();
//		t.readBTest();
		t.version2Set();
//		t.version2Verify();
//		t.version21Verify();
		t.recognizeProtocol();
//		t.readBTest();
//		t.modeegSet();		
//		t.modeegTestC();		
//		t.test();
//		t.freqTest();
//		t.brightnessTest();

//		t.writeTest();
		time = System.currentTimeMillis();
		System.out.println("Finished");
	} catch (Throwable e) {
		e.printStackTrace();
		System.out.println("error after " + (System.currentTimeMillis() - time));
		;		
	}	
}
/**
 * Tools constructor comment.
 */
public void modeegManualTest1() throws Exception {
	OutputStream out = serialPort.getOutputStream();

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();

	out.write(128+2);
	out.write(0);

	
	// Set brightness
	for (int i = 0; i < 10; i++){
		if (i % 2 == 0) {
			out.write(128+1);
			out.write(63);
			System.out.println("setting 255");
		} else {
			out.write(128+1);
			out.write(0);
			System.out.println("setting 0");
		}
		Thread.sleep(1000);
		System.out.println("i=" + i);
	}
		
	//boolean b = true;
	//while (true) {
		//if (System.in.available() > 0) {
			//int k = System.in.read();
			//if (Character.isDigit((char) k)) {
				//int thresh = k - '0';
				//out.write(128+3);
				//out.write(thresh);
				//System.out.println("Value is " + (char) ('0' + thresh));					
			//}
		//}
		
		//Thread.sleep(100);
	//}
}
/**
 * Tools constructor comment.
 */
public void modeegManualTest2() throws Exception {
	OutputStream out = serialPort.getOutputStream();

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();

	out.write(128+1);
	out.write('D');
	out.write(63);

	
	// Set brightness
	for (int i = 0; i < 10; i++){
		if (i % 2 == 0) {
			out.write(128+1);
			out.write('D');
			out.write(63);
			System.out.println("setting 255");
		} else {
			out.write(128+1);
			out.write('D');
			out.write(0);
			System.out.println("setting 0");
		}
		Thread.sleep(1000);
		System.out.println("i=" + i);
	}
		
	//boolean b = true;
	//while (true) {
		//if (System.in.available() > 0) {
			//int k = System.in.read();
			//if (Character.isDigit((char) k)) {
				//int thresh = k - '0';
				//out.write(128+3);
				//out.write(thresh);
				//System.out.println("Value is " + (char) ('0' + thresh));					
			//}
		//}
		
		//Thread.sleep(100);
	//}
}
/**
 * Tools constructor comment.
 */
public void modeegSet() throws Exception {
	OutputStream o1 = serialPort.getOutputStream();
	ModEEGBackComm cm = new ModEEGBackComm(o1);

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();

	// Set brightness
	cm.cmdPortDBitBrightOnTime(6, 63);
	cm.cmdPortDBitBrightPeriod(6, 127);
	cm.cmdPortDBitSet(6, false);
	cm.cmdPortDBitBrightOnTime(7, 63);
	cm.cmdPortDBitBrightPeriod(7, 127);
	cm.cmdPortDBitSet(7, false);
}
/**
 * Tools constructor comment.
 */
public void modeegTestA() throws Exception {
	OutputStream o1 = serialPort.getOutputStream();
	ModEEGBackComm cm = new ModEEGBackComm(o1);

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();


	
	for (int i = 0; System.in.available() == 0; i++){
		if (i % 2 == 0) {
			cm.cmdPortDSet(64);
			System.out.println("setting 255");
		} else {
			cm.cmdPortDSet(128);	
			System.out.println("setting 0");
		}
		Thread.sleep(1000);
		System.out.println("i=" + i);
	}

	
		
	//boolean b = true;
	//while (true) {
		//if (System.in.available() > 0) {
			//int k = System.in.read();
			//if (Character.isDigit((char) k)) {
				//int thresh = k - '0';
				//out.write(128+3);
				//out.write(thresh);
				//System.out.println("Value is " + (char) ('0' + thresh));					
			//}
		//}
		
		//Thread.sleep(100);
	//}
	
}
/**
 * Tools constructor comment.
 */
public void modeegTestB() throws Exception {
	OutputStream o1 = serialPort.getOutputStream();
	ModEEGBackComm cm = new ModEEGBackComm(o1);

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();

	// Set brightness
	cm.cmdPortDBitBrightOnTime(6, 3);
	cm.cmdPortDBitBrightPeriod(6, 4);
	cm.cmdPortDBitSet(6, true);
		
	boolean b = true;
	while (true) {
		if (System.in.available() > 0) {
			int k = System.in.read();
			if (Character.isDigit((char) k)) {
				int thresh = k - '0';
				cm.cmdPortDBitBrightOnTime(6, thresh);
				System.out.println("Value is " + (char) ('0' + thresh));					
			}
		}
		
		Thread.sleep(100);
	}
}
/**
 * Tools constructor comment.
 */
public void modeegTestC() throws Exception {
	OutputStream o1 = serialPort.getOutputStream();
	ModEEGBackComm cm = new ModEEGBackComm(o1);

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();

	// Set brightness
	cm.cmdPortDBitBrightOnTime(6, 63);
	cm.cmdPortDBitBrightPeriod(6, 127);
	cm.cmdPortDBitSet(6, false);
	cm.cmdPortDBitBrightOnTime(7, 63);
	cm.cmdPortDBitBrightPeriod(7, 127);
	cm.cmdPortDBitSet(7, false);
		
	boolean b = true;
	while (true) {
		if (b) {
			cm.cmdPortDBitSet(7, true);
			cm.cmdPortDBitSet(6, false);
		} else {
			cm.cmdPortDBitSet(6, true);
			cm.cmdPortDBitSet(7, false);
		}

		b = !b;
		
		if (System.in.available() > 0) {
			int k = System.in.read();
			String number = "";
			while (Character.isDigit((char) k) && System.in.available() > 0) {
				number += (char) k;
				k = System.in.read();
			}
			if (number.length() > 0) {
				System.out.println("Value is " + number);
				int thresh = Integer.parseInt(number);
				cm.cmdPortDBitBrightOnTime(6, thresh);
				cm.cmdPortDBitBrightOnTime(7, thresh);				
			}			
		}
		
		Thread.sleep(3000);
	}
}
/**
 * Tools constructor comment.
 */
public void  readBTest() throws Exception {
	InputStream in = serialPort.getInputStream();

	System.out.println("Reading...");
	for (int i = 0; i < 31; i++){
		String s = Integer.toBinaryString(in.read()).toUpperCase();
		while (s.length() < 8)
			s = "0" + s;
		System.out.println(" " + s);
	}
	System.out.println("\nReading finished");
}
/**
 * Tools constructor comment.
 */
public void  readHTest() throws Exception {
	InputStream in = serialPort.getInputStream();

	System.out.println("Reading...");
	for (int i = 0; i < 100; i++){
		String s = Integer.toHexString(in.read()).toUpperCase();
		System.out.print(" " + s);
	}
	System.out.println("\nReading finished");
}
/**
 * Tools constructor comment.
 */
public void recognizeProtocol() throws Exception {
	ModEEGBackComm bcomm = new ModEEGBackComm(serialPort.getOutputStream(), serialPort.getInputStream());

	System.out.println("Current protocol: " + bcomm.cmdGetProtocol());	
}
/**
 * Tools constructor comment.
 */
public void run() {
	try {
		InputStream in = serialPort.getInputStream();
		while (true) {
			in.read();
			readCounter++;
		}			
	} catch (Exception e) {
		System.out.println("Reading exception: " + e);
	}
	

}
/**
 * Tools constructor comment.
 */
public void test() throws Exception {
	OutputStream out = serialPort.getOutputStream();

	out.write(128);

System.out.println("ok");	
	//boolean b = true;
	//for (int i = 0; i < 1000; i++){
		//if (b)
			//out.write(128);
		//else
			//out.write(0);
		//b = !b;
		//Thread.sleep(10);
	//}
}
/**
 * Tools constructor comment.
 */
public void test1() throws Exception {
//	InputStream in = serialPort.getInputStream();
	OutputStream out = serialPort.getOutputStream();


System.out.println("out bu s=" + serialPort.getOutputBufferSize() );	
	//for (int i = 0; i < 100; i++){
		//System.out.print(" " + in.read());
	//}

	boolean s = true;
	int counter = 0;
	int v = 57600 / 8 / 2;
	int thresh = 5;
	long time = System.currentTimeMillis();
	for (int u = 0; u < 50000; u++){
		for (int i = 0; i < v; i++){
			if (System.in.available() > 0) {
				int k = System.in.read();
				if (Character.isDigit((char) k)) {
					thresh = k - '0';
					System.out.println("Theshold is " + (char) ('0' + thresh));					
				}
			}
			
			out.write('D');
			//try {
				//out.write(i);
			//} catch (Throwable e) {
				//System.out.println("failed " + i);
			//}
	//		System.out.println("ok " + i);
			if (counter > 20 - thresh)
				out.write(128);
			else
				out.write(0);
				
			if (counter++ >= 18)
				counter = 0;

			out.flush();
			Thread.sleep(1);
		}
	}
	
//	out.close();
	System.out.println("time=" + (System.currentTimeMillis() - time));
}
/**
 * Tools constructor comment.
 */
public void version21Test() throws Exception {
//	version2Verify();
	ModEEGBackComm comm = new ModEEGBackComm(serialPort.getOutputStream());
	comm.cmdSetProtocol(22);
	comm.cmdSetChannelMatrix21(1);
	int n = serialPort.getInputStream().available();
	byte b[] = new byte[n];
	serialPort.getInputStream().read(b);
	comm.cmdGetProtocol();
	
	readBTest();

	
}
/**
 * Tools constructor comment.
 */
public void version21Verify() throws Exception {
	ProtocolV21InputStream el = new ProtocolV21InputStream(serialPort.getInputStream());
	ModEEGBackComm bcomm = new ModEEGBackComm(serialPort.getOutputStream(), serialPort.getInputStream());

	bcomm.cmdSetProtocol(21);

	System.out.println("Protocol set to 21, reading ...");		
	
	System.out.println("Current protocol: " + bcomm.cmdGetProtocol());	
	
	int n = 200;
	for (int i = 0; i < n; i++){
		el.readPacket();
	}
	if (el.lostPackets == 0)
		System.out.println("Read " + n + " packets from modeeg v21, OK");
	else
		System.out.println(" ---- FAILURE --- Lost " + el.lostPackets + " packets from modeeg v21");	
}
/**
 * Tools constructor comment.
 */
public void version21VerifyMatrix() throws Exception {
	ProtocolV21InputStream el = new ProtocolV21InputStream(serialPort.getInputStream());
	ModEEGBackComm bcomm = new ModEEGBackComm(serialPort.getOutputStream(), serialPort.getInputStream());

	bcomm.cmdSetProtocol(21);
	bcomm.cmdSetChannelMatrix21(4+2);

	bcomm.flushInputStream();
	bcomm.cmd21GetVInfo(bcomm.VINFO_CHANNELS_MATRIX, false);
	readBTest();
	
	System.out.println("Current protocol: " + bcomm.cmdGetProtocol());
	int matrix  = bcomm.cmdGetMatrix();
	System.out.println("Current matrix: " + matrix + " (" + Integer.toBinaryString(matrix) + ")");	
	
}
/**
 * Tools constructor comment.
 */
public void version2Set() throws Exception {
	ProtocolV21InputStream el = new ProtocolV21InputStream(serialPort.getInputStream());
	ModEEGBackComm bcomm = new ModEEGBackComm(serialPort.getOutputStream(), serialPort.getInputStream());
	bcomm.cmdSetProtocol(2);
	
	System.out.println("Protocol 2 set");
}
/**
 * Tools constructor comment.
 */
public void version2Verify() throws Exception {
	ElecGuruInputStream el = new ElecGuruInputStream(serialPort.getInputStream());
	int n = 20;
	for (int i = 0; i < n; i++){
		el.readPacket();
	}
	if (el.lostPackets == 0)
		System.out.println("Read " + n + " packets from modeeg v2, OK");
	else
		System.out.println(" ---- FAILURE --- Lost " + el.lostPackets + " packets from modeeg v2");
}
/**
 * Tools constructor comment.
 */
public void writeTest() throws Exception {
	OutputStream out = serialPort.getOutputStream();

	Thread t = new Thread(this);
	t.setDaemon(true);
	t.start();
	
	System.out.println("reset");

	boolean b = false;
	for (int i = 0; i < 10000; i++){
		System.out.println("writing1");
		out.write(128 + 1);
		//System.out.println("writing2");
		out.write(b ? 0 : 32);
		//System.out.println("wrote");
		out.flush();
		b = !b;			
		Thread.sleep(1000);
	}

//	System.out.println("set");	
		
	out.write(128 + 1);
	out.write(32);
	out.flush();
	
	
	System.out.println("wrote finished");	
	//boolean b = true;
	//for (int i = 0; i < 1000; i++){
		//if (b)
			//out.write(128);
		//else
			//out.write(0);
		//b = !b;
		//Thread.sleep(10);
	//}
}
}
