/* SerialPort.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.processing.impl;


import java.io.*;
import java.util.*;
import bioera.processing.*;
import bioera.fft.*;
import bioera.storage.*;
import bioera.*;
import bioera.properties.*;
import bioera.serial.*;
import bioera.graph.runtime.RuntimeFrame;
import bioera.*;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SerialPort extends AbstractStreamElement {
        public static ComboProperty port = new ComboProperty(new String[] {
                "none"
        });


        public ComboProperty baud = new ComboProperty(new String[] {
                "none"
        });


        public ComboProperty dataBits = new ComboProperty(new String[] {
                "none"
        });

        public ComboProperty stopBits = new ComboProperty(new String[] {
                "none"
        });

        public ComboProperty parity = new ComboProperty(new String[] {
                "none"
        });

        public ComboProperty flowControl = new ComboProperty(new String[] {
                "none"
        });

        public ComboProperty implementation = new ComboProperty(new String[] {
                "javax.comm",
        });

        public boolean DTR = true;
        public boolean RTS = true;

        private final static String propertiesDescriptions[][] = {
                {"implementation", "Implementation", ""},
                {"port", "Port", ""},
                {"baud", "Baud", ""},
                {"dataBits", "Data bits", ""},
                {"stopBits", "Stop bits", ""},
                {"parity", "Parity", ""},
                {"flowControl", "Flow control", ""},
        };


        //private Object serialHandler;
        //SerialPortImpl impl;
        private boolean firstRun=true;
        protected static boolean debug = bioera.Debugger.get("impl.serial.port");




        // This debugging option is to help with testing of ready designs
        // without need to connect to a serial device
        //private static boolean readFromSocket = bioera.Debugger.get("impl.serial.port.socket");
/**
 * SerialDeviceSource constructor comment.
 */
public SerialPort() {
        super();

        setName("Serial");
}
/**
 * Element constructor comment.
 */
public void close() throws Exception {
        super.close();

        if (SerialMain.impl != null) {
                SerialMain.impl.close();
        }
}


/**
 * Element constructor comment.
 */
public String getElementDescription() {
        return "Serial port device reader/writer";
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




public void initPort() throws Exception {
        //Main.app.impl.close();
        //call initport in serialmain

        SerialMain.init();
}
/**
 * Element constructor comment.
 */
public static void changePort(int n) throws Exception {
    port.setSelectedItem("COM"+n);
}

public static String getPort() throws Exception {
    String selectedport = port.getSelectedItem();
    return selectedport;
}


public void reinit() throws Exception {


        //Main.app.impl = SerialPortFactory.getInstance("javax.comm");
        //Main.app.impl.debug = this.debug;
        //Main.app.impl.init();

/*
        Main.app.impl = SerialPortFactory.getInstance("javax.comm");
        Main.app.impl.init();
  */


        //Main.app.impl.close();  //this can go into init in SerialMain
        //port.setItems(SerialMain.impl.ports); //get com port options for combobox


        SerialMain.impl.init();

        streamIn = SerialMain.streamIn;
        streamOut = SerialMain.streamOut;

        port.setItems(SerialMain.impl.ports);



        //below will load last com port used from settings file and set it as selected in pulldown only on first run
        if ((port.getSelectedIndex() != -1) && (firstRun == true)) {
            //set to port from registry or from saved file
            port.setSelectedItem("COM" + String.valueOf(SerialMain.lastPortNum));

            //String currPort = port.getSelectedItem();

            if (!Main.app.osName.startsWith("Windows")) {

                if (SerialMain.lastPortNum == 1)
                    Main.app.runtimeFrame.com1MenuItem.setSelected(true);
                else if (SerialMain.lastPortNum == 2)
                    Main.app.runtimeFrame.com2MenuItem.setSelected(true);
                else if (SerialMain.lastPortNum == 3)
                    Main.app.runtimeFrame.com3MenuItem.setSelected(true);
                else if (SerialMain.lastPortNum == 4)
                    Main.app.runtimeFrame.com4MenuItem.setSelected(true);
                else if (SerialMain.lastPortNum == 5)
                    Main.app.runtimeFrame.com5MenuItem.setSelected(true);
                else if (SerialMain.lastPortNum == 6)
                    Main.app.runtimeFrame.com6MenuItem.setSelected(true);
                else if (SerialMain.lastPortNum == 7)
                    Main.app.runtimeFrame.com7MenuItem.setSelected(true);
                else if (SerialMain.lastPortNum == 8)
                    Main.app.runtimeFrame.com8MenuItem.setSelected(true);
            }
            firstRun = false;
        }

        if (port.getSelectedIndex() == -1)
                port.setSelectedIndex(0);
        baud.setItems(SerialMain.impl.bauds);
        if (baud.getSelectedIndex() == -1)
                baud.setSelectedItem("19200");
        if (baud.getSelectedIndex() == -1)
                baud.setSelectedIndex(0);
        dataBits.setItems(SerialMain.impl.dataBits);
        if (dataBits.getSelectedIndex() == -1)
                dataBits.setSelectedItem("8");
        if (dataBits.getSelectedIndex() == -1)
                dataBits.setSelectedIndex(0);
        stopBits.setItems(SerialMain.impl.stopBits);
        if (stopBits.getSelectedIndex() == -1)
                stopBits.setSelectedItem("1");
        if (stopBits.getSelectedIndex() == -1)
                stopBits.setSelectedIndex(0);
        parity.setItems(SerialMain.impl.parity);
        if (parity.getSelectedIndex() == -1)
                parity.setSelectedItem("NONE");
        if (parity.getSelectedIndex() == -1)
                parity.setSelectedIndex(0);
        flowControl.setItems(SerialMain.impl.flowControl);
        if (flowControl.getSelectedIndex() == -1)
                flowControl.setSelectedItem("NONE");
        if (flowControl.getSelectedIndex() == -1)
                flowControl.setSelectedIndex(0);


        // Init the port to check if it works
        ////initPort();
        //SerialMain.init();

        // And close it now, it will be reopen when processing starts
        //close(); //mmods

        super.reinit();

        
       //getStatus
    if (Main.gotStatus == false) {
            Thread.sleep(3200);
            SerialMain.getStatus();
    }        
        
/*   method to get tables now moved 
 *   to tableEditor
        if (Main.gotTables == false) {
                Thread.sleep(3200);
                SerialMain.getTables();
        }
*/

 }
/**
 * Insert the method's description here.
 * Creation date: (10/10/2003 10:11:07 PM)
 */
public void start() throws java.lang.Exception {

        //initPort(); //mmods
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2003 10:11:07 PM)
 */
public void stop() throws java.lang.Exception {
        //close(); //mmods
}
    /*
public static void setReadFromSocket(boolean newValue) {
        readFromSocket = newValue;
}*/
}
