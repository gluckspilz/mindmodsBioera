/* SerialTest.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.tests;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.comm.*;
//import gnu.io.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SerialTest  
{
    public SerialTest()
    {
    }
    public static void main(String args[])
        throws Exception
    {
        try
        {
            SerialTest t = new SerialTest();
            //t.start();
            t.readBin("COM1");
            System.out.println("Finished");
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e + "\n\n");
            e.printStackTrace();
        }
    }
public void start() throws Exception {
	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	CommPortIdentifier portId = null;

	boolean any = false;
	while (portList.hasMoreElements()) {
		any = true;
		portId = (CommPortIdentifier) portList.nextElement();
		System.out.println("Port: " + portId.getName());
		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			System.out.println("Serial port '" + portId.getName() + "'");
		} else {
			System.out.println("NonSerial port '" + portId.getName() + "'");
		}
	}

	if (!any)
		System.out.println("Not found any comm device");
}

public String read(String deviceName) throws Exception {
	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	CommPortIdentifier portId = null;
    
	while (portList.hasMoreElements()) {
		portId = (CommPortIdentifier) portList.nextElement();
		//System.out.println("Port: " + portId.getName());
		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			if (portId.getName().equals(deviceName)) {
				// Found the port
				break;
			}
		}
		portId = null;
	}

	if (portId == null)
		throw new Exception("Not found port '" + deviceName + "'");

	SerialPort serialPort;
	try {
		serialPort = (SerialPort) portId.open("SerialPort", 2000);
	} catch (PortInUseException e) {
		throw new Exception("Port '" + deviceName + "' is already in use");
	}

	serialPort.notifyOnDataAvailable(false);
	
	try {		
		serialPort.setSerialPortParams(38400, 8, 1, SerialPort.PARITY_EVEN);
	} catch (UnsupportedCommOperationException e) {
		throw new Exception("Unexpected serial port initialization error: " + e);
	}

	InputStream streamIn = serialPort.getInputStream();

	// Flush input buffer of serial port
	int n = streamIn.available();
	for (int i = 0; i < n; i++){
		streamIn.read();
	}

	//streamOut = serialPort.getOutputStream();
		
	System.out.println("Serial port " + portId.getName() + " initialized");

		
	for (int i = 0; i < 30; i++){
		String s = Integer.toBinaryString(streamIn.read());
		while (s.length() < 8)
			s = "0" + s;
		System.out.println("" + s);
	}

	return null;
}

public String readBin(String deviceName) throws Exception {
	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	CommPortIdentifier portId = null;
    
	while (portList.hasMoreElements()) {
		portId = (CommPortIdentifier) portList.nextElement();
		//System.out.println("Port: " + portId.getName());
		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			if (portId.getName().equals(deviceName)) {
				// Found the port
				break;
			}
		}
		portId = null;
	}

	if (portId == null)
		throw new Exception("Not found port '" + deviceName + "'");

	SerialPort serialPort;
	try {
		serialPort = (SerialPort) portId.open("SerialPort", 2000);
	} catch (PortInUseException e) {
		throw new Exception("Port '" + deviceName + "' is already in use");
	}

	serialPort.notifyOnDataAvailable(false);
	
	try {		
		serialPort.setSerialPortParams(38400, 8, 1, SerialPort.PARITY_EVEN);
	} catch (UnsupportedCommOperationException e) {
		throw new Exception("Unexpected serial port initialization error: " + e);
	}

	InputStream streamIn = serialPort.getInputStream();

	// Flush input buffer of serial port
	int n = streamIn.available();
	for (int i = 0; i < n; i++){
		streamIn.read();
	}

	//streamOut = serialPort.getOutputStream();
		
	System.out.println("Serial port " + portId.getName() + " initialized");

		
	for (int i = 0; i < 300000; i++){
		//String s = Integer.toBinaryString(streamIn.read());
		//while (s.length() < 8)
		//	s = "0" + s;
		System.out.write(streamIn.read());
	}

	return null;
}
}
