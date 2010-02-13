/* JavaxCommImpl.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.serial.*;
import java.util.*;
import javax.comm.*;


/**
 * Insert the type's description here.
 * Creation date: (5/2/2004 7:03:03 PM)
 * @author: Jarek
 */
public class JavaxCommImpl extends SerialPortImpl {
	SerialPort serialPort;
/**
 * JavaxComm constructor comment.
 */
public JavaxCommImpl() {
	super();
}
/**
 * SerialPortImpl constructor comment.
 */
public void close() throws java.lang.Exception {
	if (serialPort != null) {
		serialPort.close();
		serialPort = null;
	}
}
/**
 * SerialPortImpl constructor comment.
 */
public String connect(java.lang.String port, java.lang.String baud, java.lang.String dB, java.lang.String sB, java.lang.String pB, String fB) throws Exception {
	close();

	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	CommPortIdentifier portId = null;

	while (portList.hasMoreElements()) {
		portId = (CommPortIdentifier) portList.nextElement();
		//System.out.println("Port: " + portId.getName());
		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			if (portId.getName().equals(port)) {
				// Found the port
				break;
			}
		}
		portId = null;
	}

	if (portId == null)
		return "Not found port '" + port + "'";

	try {
		serialPortObj = serialPort = (SerialPort) portId.open("PreSage", 2000);
	} catch (PortInUseException e) {
		return "Port '" + port + "' is already in use";
	}

	//serialPort.notifyOnBreakInterrupt(false) ;
	//serialPort.notifyOnCarrierDetect(false);
	//serialPort.notifyOnCTS(false);
	serialPort.notifyOnDataAvailable(false);
	//serialPort.notifyOnDSR(false);
	//serialPort.notifyOnFramingError(false);
	//serialPort.notifyOnOutputEmpty(false);
	//serialPort.notifyOnOverrunError(false);
	//serialPort.notifyOnParityError(false);
	//serialPort.notifyOnRingIndicator(false);

	try {
		// This mode is not implemented in javax.comm
		if ("SPACE".equals(pB) || "MARK".equals(pB))
			pB = "EVEN";
                serialPort.setSerialPortParams(19200, fieldToInt("DATABITS_" + "8"), fieldToInt("STOPBITS_" + "1"), fieldToInt("PARITY_" + "NONE"));
		serialPort.setFlowControlMode(fieldToInt("FLOWCONTROL_" + "NONE"));
	} catch (UnsupportedCommOperationException e) {
		return "Unexpected serial port initialization error: " + e;
	}

	in = serialPort.getInputStream();

	// Flush input buffer of serial port
	int n = in.available();
	for (int i = 0; i < n; i++){
		in.read();
	}

	out = serialPort.getOutputStream();

	if (debug)
		System.out.println("Serial port " + portId.getName() + " initialized");

	return null;
}
/**
 * SerialPortImpl constructor comment.
 */
public void init() throws Exception {
	List list = new ArrayList();
	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	CommPortIdentifier portId = null;

	while (portList.hasMoreElements()) {
		portId = (CommPortIdentifier) portList.nextElement();
		//System.out.println("Port: " + portId.getName());
		if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			list.add(portId.getName());
		}
	}

	ports = (String[]) list.toArray(new String[0]);

	super.init(SerialPort.class);
}

/**
 * SerialPortImpl constructor comment.
 */
public void setDTR(boolean s) throws java.lang.Exception {
	serialPort.setDTR(s);
}

/**
 * SerialPortImpl constructor comment.
 */
public void setRTS(boolean s) throws java.lang.Exception {
	serialPort.setRTS(s);
}
}
