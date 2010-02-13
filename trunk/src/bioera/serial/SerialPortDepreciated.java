/* SerialPortDepreciated.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.serial;

import java.io.*;
import java.util.*;
import javax.comm.*;
import javax.comm.SerialPort;
import bioera.processing.*;
import bioera.fft.*;
import bioera.graph.designer.*;
import bioera.storage.*;
import bioera.*;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SerialPortDepreciated extends AbstractStreamElement {
	public String serialDevice = "COM1";
	public int bitsPerSecond = 57600;
	public int dataBits = SerialPort.DATABITS_8;
	public int stopBits = SerialPort.STOPBITS_1;
	public int parity = SerialPort.PARITY_NONE;
	public String devicePath;

	private final static String propertiesDescriptions[][] = {
//		{"frequencies", "Frequency array [>0]", ""},
	};


	private SerialPort serialPort;
	protected static boolean debug = bioera.Debugger.get("impl.serial.port");

	// This debugging option should help with testing ready designs
	// Without need to connect to a device
	private static boolean readFromSocket = bioera.Debugger.get("impl.serial.port.socket");
/**
 * SerialDeviceSource constructor comment.
 */
public SerialPortDepreciated() {
	super();

	setName("Serial");
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
	super.close();

	if (serialPort != null) {
		serialPort.close();
		serialPort = null;
	}
}
/**
 * Element constructor comment.
 */
public String getElementDescription() {
	return "Input/output stream can be read/written from/to serial port.";
}
/**
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
private void initPort() throws Exception {
	close();

	if (readFromSocket) {
		System.out.println("Trying serial from socket");
		// Read from a socket, not serial port
		try {
			java.net.Socket s = new java.net.Socket("localhost", 9283);
			streamIn = s.getInputStream();
			streamOut = s.getOutputStream();
			return;
		} catch (Exception e) {
			System.out.println("Alternative serial port socket source read failed");
		}
	}

	if (!TOOLS.isNone(serialDevice)) {
		if (debug)
			System.out.println("Trying java comm");
		String errMsg = tryJavaComm();
		if (errMsg == null) {
			if (debug)
				System.out.println("Java comm initialized ok");
			// ok, serial port was initialized
		} else {
			if (!TOOLS.isNone(devicePath)) {
				// See if the devicePath works
				if (trySerialPath() != null)
					throw new DesignException("Could not initialize serial device: " + serialDevice);
				else if (debug)
					System.out.println("Serial path initialized ok");
			} else {
				throw new DesignException("Java serial port initialization error: " + errMsg);
			}
		}
	} else if (!TOOLS.isNone(devicePath)) {
		String msg = trySerialPath();
		if (msg != null)
			throw new DesignException("Couldn't initialize serial device path: " + msg);
	} else
		throw new DesignException("Required settings not set");

	super.reinit();
}
/**
 * Element constructor comment.
 */
public void reinit() throws Exception {
	// Either com port must be specified (Windows) or path to device (Unix)
	verifyDesignState(!TOOLS.isNone(serialDevice) || !TOOLS.isNone(devicePath));

	// Init the port to check if it works
	initPort();

	// And close it now, it will be reopen when processing starts
	close();

	super.reinit();
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2003 10:11:07 PM)
 */
public void start() throws java.lang.Exception {
	initPort();
	elementIn.purgeAll();
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2003 10:11:07 PM)
 */
public void stop() throws java.lang.Exception {
	close();
}
/**
 * Element constructor comment.
 */
private String tryJavaComm() throws Exception {
	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	CommPortIdentifier portId = null;

	while (portList.hasMoreElements()) {
		portId = (CommPortIdentifier) portList.nextElement();
		//System.out.println("Port: " + portId.getName());
		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			if (portId.getName().equals(serialDevice)) {
				// Found the port
				break;
			}
		}
		portId = null;
	}

	if (portId == null)
		return "Not found port '" + serialDevice + "'";

	try {
		serialPort = (SerialPort) portId.open("SerialPort", 2000);
		serialPort.setDTR(true);
		serialPort.setRTS(true);
//		System.out.println("set to 2");
	} catch (PortInUseException e) {
		return "Port '" + serialDevice + "' is already in use";
	}

	serialPort.notifyOnDataAvailable(false);

	try {
		serialPort.setSerialPortParams(bitsPerSecond, dataBits, stopBits, parity);
	} catch (UnsupportedCommOperationException e) {
		return "Unexpected serial port initialization error: " + e;
	}

	streamIn = serialPort.getInputStream();

	// Flush input buffer of serial port
	int n = streamIn.available();
	for (int i = 0; i < n; i++){
		streamIn.read();
	}

	streamOut = serialPort.getOutputStream();

	if (debug)
		System.out.println("Serial port " + portId.getName() + " initialized");

	return null;

/*
public class SimpleRead implements Runnable, SerialPortEventListener {
    static CommPortIdentifier portId;
    static Enumeration portList;

    InputStream inputStream;
    SerialPort serialPort;
    Thread readThread;

    public static void main(String[] args) {
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals("COM1")) {
                //if (portId.getName().equals("/dev/term/a")) {
                    SimpleRead reader = new SimpleRead();
                }
            }
        }
    }

    public SimpleRead() {
        try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
        } catch (PortInUseException e) {}
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {}
	try {
            serialPort.addEventListener(this);
	} catch (TooManyListenersException e) {}
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(57600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {}
        readThread = new Thread(this);
        readThread.start();
    }

    public void run() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {}
    }

    public void serialEvent(SerialPortEvent event) {
        switch(event.getEventType()) {
        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            break;
        case SerialPortEvent.DATA_AVAILABLE:
            byte[] readBuffer = new byte[20];

            try {
                while (inputStream.available() > 0) {
                    int numBytes = inputStream.read(readBuffer);
		    System.out.write(readBuffer, 0, numBytes);
                }
            } catch (IOException e) {}
            break;
        }
    }
}
*/
}
/**
 * Element constructor comment.
 */
private String trySerialPath() throws Exception {
	if (devicePath == null || !new File(devicePath).exists()) {
		return "Device " + devicePath + " not found";
	} else {
		streamIn = new FileInputStream(devicePath);
		streamOut = new FileOutputStream(devicePath);
		return null;
	}
}
public static void setReadFromSocket(boolean newValue) {
	readFromSocket = newValue;
}
}
