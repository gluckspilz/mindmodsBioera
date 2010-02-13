/* RuntimeEventHandler.java v 1.0.9   11/6/04 7:15 PM
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

package bioera.graph.runtime;



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import bioera.*;
import bioera.graph.chart.*;
import bioera.processing.*;
import bioera.graph.runtime.*;
import bioera.properties.*;
import bioera.graph.designer.*;
import bioera.processing.impl.SerialPort;
import bioera.serial.*;
import java.io.File;
import bioera.Log;
import java.io.FileInputStream;
import bioera.serial.SerialPortFactory;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class RuntimeEventHandler implements MouseListener, MouseMotionListener, ActionListener, WindowListener, ComponentListener, KeyListener {


	//RuntimeFrame runtimeframe;
	int x,y;
	Component component;
        JGraphPanel panel;
        GraphPanel panel2;
        boolean disposed = false;
        public String prevPortCommand = null;
	protected static boolean debug = bioera.Debugger.get("runtime.eventhandler");
        File filemngrdesign = null;
        private static int sessFileinLeng = 0; //just made static

        String designName;
/**
 * MouseDebug constructor comment.
 */
public RuntimeEventHandler(RuntimeFrame f, GraphPanel p) {
        super();
        Main.app.runtimeFrame = f;
        panel2 = p;
}
public RuntimeEventHandler(RuntimeFrame f, JGraphPanel p) {
    super();
    Main.app.runtimeFrame = f;
    panel = p;
}
	/**
	 * Invoked when an action occurs.
	 */


private void unselectLastPortItem(String prevPortCommand) {
try{

    if (prevPortCommand.charAt(3) == "1".charAt(0)) {
        Main.app.runtimeFrame.com1MenuItem.setSelected(false);
    }
    if (prevPortCommand.charAt(3) == "2".charAt(0)) {
        Main.app.runtimeFrame.com2MenuItem.setSelected(false);
    }
    if (prevPortCommand.charAt(3) == "3".charAt(0)) {
        Main.app.runtimeFrame.com3MenuItem.setSelected(false);
    }
    if (prevPortCommand.charAt(3) == "4".charAt(0)) {
        Main.app.runtimeFrame.com4MenuItem.setSelected(false);
    }
    if (prevPortCommand.charAt(3) == "5".charAt(0)) {
        Main.app.runtimeFrame.com5MenuItem.setSelected(false);
    }
    if (prevPortCommand.charAt(3) == "6".charAt(0)) {
        Main.app.runtimeFrame.com6MenuItem.setSelected(false);
    }
    if (prevPortCommand.charAt(3) == "7".charAt(0)) {
        Main.app.runtimeFrame.com7MenuItem.setSelected(false);
    }
    if (prevPortCommand.charAt(3) == "8".charAt(0)) {
        Main.app.runtimeFrame.com8MenuItem.setSelected(false);
    }
}
catch(Exception Exc){Exc.printStackTrace();}
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            String command = e.getActionCommand().toLowerCase();
            String command2 = e.paramString();
            if (prevPortCommand == null) {
                //prevPortCommand = getCurrentPort();
                prevPortCommand = "COM"+String.valueOf(SerialMain.lastPortNum);
            }

            //System.out.println("command: '" + command + "' " + command.getClass() + " " + e.getSource().getClass());

            //Component c = (Component) e.getSource();
            //System.out.println("x=" + c.getX() + "  y=" + c.getY());
            /*
              if ("properties".equals(command)) {
               if (component instanceof ChartComponent) {
                ChartComponent canvas = (ChartComponent) component;
                try {
                 System.out.println("dialog created");
                 bioera.properties.PropertiesDialog dialog = new bioera.properties.PropertiesDialog(runtimeframe.frame, canvas.getChart().getElement());
                 dialog.pack();
                 dialog.show();
                } catch (Exception ex) {
                 ex.printStackTrace();
                }
               } else {
                System.out.println("class=" + e.getSource().getClass());
               }
              }*/

            if ("start".equals(command)) {
                if (Main.app != null) {
                    Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                    Main.app.runtimeFrame.stopMenuItem.setEnabled(true);
                    Main.app.runtimeFrame.tempcMenuItem.setEnabled(false);
                    Main.app.runtimeFrame.tempfMenuItem.setEnabled(false);
                    Main.app.runtimeFrame.saveMenuItem.setEnabled(false);

                    try{
                    Main.app.runtimeFrame.setToolBarButton("Start", false);
                    Main.app.runtimeFrame.setToolBarButton("Stop", true);
                    Main.app.runtimeFrame.setToolBarButton("Session Manager", false);
                    Main.app.runtimeFrame.setToolBarButton("Table Editor", false);
                    Main.app.runtimeFrame.statusLengthf.setText( "  " );  //seslength
                    Main.app.runtimeFrame.statusModef.setText( "  "  );  //mode
                    Main.app.runtimeFrame.statusSensf.setText( "  " );  //sensitivity
                    Main.app.runtimeFrame.statusProbef.setText(  "  "  );  //probeconf
                    Main.app.runtimeFrame.statusTimerf.setText(  "  "  );  //timer

                }
                    catch(Exception ex){}
                    Main.app.processor.startProcessing();
                }
            } else if ("stop".equals(command)) {
                if (Main.app != null) {
                    Main.app.runtimeFrame.sensBar.setVisible(false);
                    Main.app.runtimeFrame.sensMeter.setVisible(false);
                    Main.app.runtimeFrame.startMenuItem.setEnabled(true);
                    Main.app.runtimeFrame.stopMenuItem.setEnabled(false);
                    Main.app.runtimeFrame.tempcMenuItem.setEnabled(true);
                    Main.app.runtimeFrame.tempfMenuItem.setEnabled(true);
                    try{
                    Main.app.runtimeFrame.setToolBarButton("Start", true);
                    Main.app.runtimeFrame.setToolBarButton("Stop", false);
                    Main.app.runtimeFrame.setToolBarButton("Session Manager", true);
                    Main.app.runtimeFrame.setToolBarButton("Table Editor", true);

                }
                    catch(Exception ex){}
                    bioera.device.impl.Presagedevice.sendStopCMD();
                    Main.app.runtimeFrame.progressBar.setVisible(false);
                    Main.app.runtimeFrame.calibrateButton.setVisible(false);
                    Main.app.processor.stopProcessing();
                }
            } else if ("re-initialize".equals(command)) {
                if (Main.app != null) {
                    Main.app.runtimeFrame.sensBar.setVisible(false);
                    Main.app.runtimeFrame.sensMeter.setVisible(false);
                    Main.app.runtimeFrame.startMenuItem.setEnabled(true);
                    Main.app.runtimeFrame.stopMenuItem.setEnabled(false);
                    try{
                    Main.app.runtimeFrame.progressBar.setVisible(false);
                    Main.app.runtimeFrame.calibrateButton.setVisible(false);
                    Main.app.runtimeFrame.setToolBarButton("Start", true);
                    Main.app.runtimeFrame.setToolBarButton("Stop", false);
                    Main.app.runtimeFrame.setToolBarButton("Session Manager", true);
                    Main.app.runtimeFrame.setToolBarButton("Table Editor", true);
                    Main.app.runtimeFrame.statusLengthf.setText( "  " );  //seslength
                    Main.app.runtimeFrame.statusModef.setText( "  "  );  //mode
                    Main.app.runtimeFrame.statusSensf.setText( "  " );  //sensitivity
                    Main.app.runtimeFrame.statusProbef.setText(  "  "  );  //probeconf
                    Main.app.runtimeFrame.statusTimerf.setText(  "  "  );  //timer
                    if (Main.gotTables==true){
                        Main.app.runtimeFrame.changeConnIcon(1);
                    }
                }
                    catch(Exception ex){}
                    if (Main.app.processor.isProcessing()) {
                        bioera.device.impl.Presagedevice.sendStopCMD();
                        Main.app.processor.stopProcessing();
                    }
                    try {
                        Main.app.processor.repaintGraphics();
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                    ;
                    try {
                        SerialMain.init();
                        Main.app.processor.initializeAll();
                    } catch (Exception E) {
                        E.printStackTrace();
                    }

                }
            } else if ("open".equals(command)) {
                if (Main.app != null) {
                    try {

                    javax.swing.JFileChooser d = new javax.swing.JFileChooser(Main.app.getSessionsFolder());

                    d.setCurrentDirectory(Main.app.getSessionsFolder());
                    d.setDialogTitle("Open Session");
                    d.addChoosableFileFilter(new ExtFileFilter("PreSage Dual Mode Sessions (Shift+Click for Multiple Sessions)", "se2"));
                    d.addChoosableFileFilter(new ExtFileFilter("PreSage Single Mode Sessions (Shift+Click for Multiple Sessions)", "se1"));


                    d.setCurrentDirectory(Main.app.getSessionsFolder());
                    //d.setAcceptAllFileFilterUsed(false);

                    //d.setFileSelectionMode(d.FILES_ONLY); //keep?
                    d.setMultiSelectionEnabled(true);

                    int ret = d.showOpenDialog(Main.app.runtimeFrame.frame);
                    if (ret == d.APPROVE_OPTION) {
                        java.io.File[] f = d.getSelectedFiles();

                        Main.app.runtimeFrame.loadSession(f);

                    }



                    }
                    catch (Exception E) {
                        E.printStackTrace();
                    }
                }
            } else if ("save session".equals(command)) {
                try{
                    javax.swing.JFileChooser d = new javax.swing.JFileChooser();

                    d.setFileSelectionMode(d.FILES_ONLY);
                    d.setCurrentDirectory(Main.app.getSessionsFolder());
                    d.setDialogTitle("Save Completed Session");
                    String ext = new String();
                    if ( Main.app.dualProbe==false ) {
                        ext = "se1";
                        d.setFileFilter(new ExtFileFilter("Single Probe Sessions", ext));
                    }
                    else {
                        ext = "se2";
                        d.setFileFilter(new ExtFileFilter("Dual Probe Sessions", ext));
                    }  //discover save extension based on session type

                    int ret = d.showSaveDialog(Main.app.runtimeFrame.frame);
                    if (ret == d.APPROVE_OPTION) {
                        java.io.File f = d.getSelectedFile();
                        if (f != null) {
                            if (!f.getName().toLowerCase().endsWith("."+ext))
                                f = new java.io.File(f.getPath() + "."+ext);
                            try {

                                Main.app.completedfilename.renameTo(f);
                            } catch (Exception ex) {
                                Main.app.runtimeFrame.errorDialog(102);
                            }
                        }
                    }
                }
                catch(Exception Exc){Exc.printStackTrace();};
            } else if ("print".equals(command)) {
            } else if ("table editor".equals(command)) {
                try {
                    //Main.app.runtimeFrame.tableEditor();
                    Main.app.runtimeFrame.frame.setVisible(false);
                    Main.app.toggleTableEditor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("com1".equals(command)) {
                if (Main.app != null) {
                    unselectLastPortItem(prevPortCommand);
                    try {
                        ((JMenuItem) e.getSource()).setSelected(true);
                        Main.gotTables=false;Main.presageTable0[0] = 0;
                        SerialPort.port.setSelectedItem("COM1");
                        Main.app.processor.reInitializeAll();
                        Main.app.processor.repaintGraphics();
                        prevPortCommand = "COM1";
                        Main.app.runtimeFrame.changeConnIcon(0);
                        try {
                            Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                            Main.app.runtimeFrame.setToolBarButton("Start", false);
                            Main.app.runtimeFrame.setToolBarButton("Stop", true);
                        } catch (Exception ex) {}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            } else if ("com2".equals(command)) {
                if (Main.app != null) {
                    unselectLastPortItem(prevPortCommand);
                    try {
                        ((JMenuItem) e.getSource()).setSelected(true);
                        Main.gotTables=false;Main.presageTable0[0] = 0;
                        SerialPort.port.setSelectedItem("COM2");
                        Main.app.processor.reInitializeAll();
                        Main.app.processor.repaintGraphics();
                        prevPortCommand = "COM2";
                        Main.app.runtimeFrame.changeConnIcon(0);
                        try {
                            Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                            Main.app.runtimeFrame.setToolBarButton("Start", false);
                            Main.app.runtimeFrame.setToolBarButton("Stop", true);
                        } catch (Exception ex) {}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            } else if ("com3".equals(command)) {
                if (Main.app != null) {
                    unselectLastPortItem(prevPortCommand);
                    try {
                        ((JMenuItem) e.getSource()).setSelected(true);
                        Main.gotTables=false;Main.presageTable0[0] = 0;
                        SerialPort.port.setSelectedItem("COM3");
                        Main.app.processor.reInitializeAll();
                        Main.app.processor.repaintGraphics();
                        prevPortCommand = "COM3";
                        Main.app.runtimeFrame.changeConnIcon(0);
                        try {
                            Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                            Main.app.runtimeFrame.setToolBarButton("Start", false);
                            Main.app.runtimeFrame.setToolBarButton("Stop", true);
                        } catch (Exception ex) {}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if ("com4".equals(command)) {
                if (Main.app != null) {
                    unselectLastPortItem(prevPortCommand);
                    try {
                        ((JMenuItem) e.getSource()).setSelected(true);
                        Main.gotTables=false;Main.presageTable0[0] = 0;
                        SerialPort.port.setSelectedItem("COM4");
                        Main.app.processor.reInitializeAll();
                        Main.app.processor.repaintGraphics();
                        prevPortCommand = "COM4";
                        Main.app.runtimeFrame.changeConnIcon(0);
                        try {
                            Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                            Main.app.runtimeFrame.setToolBarButton("Start", false);
                            Main.app.runtimeFrame.setToolBarButton("Stop", true);
                        } catch (Exception ex) {}

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if ("com5".equals(command)) {
                if (Main.app != null) {
                    unselectLastPortItem(prevPortCommand);
                    try {
                        ((JMenuItem) e.getSource()).setSelected(true);
                        Main.gotTables=false;Main.presageTable0[0] = 0;
                        SerialPort.port.setSelectedItem("COM5");
                        Main.app.processor.reInitializeAll();
                        Main.app.processor.repaintGraphics();
                        prevPortCommand = "COM5";
                        Main.app.runtimeFrame.changeConnIcon(0);
                        try {
                            Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                            Main.app.runtimeFrame.setToolBarButton("Start", false);
                            Main.app.runtimeFrame.setToolBarButton("Stop", true);
                        } catch (Exception ex) {}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if ("com6".equals(command)) {
                if (Main.app != null) {
                    unselectLastPortItem(prevPortCommand);
                    try {
                        Main.gotTables=false;Main.presageTable0[0] = 0;
                        ((JMenuItem) e.getSource()).setSelected(true);
                        SerialPort.port.setSelectedItem("COM6");
                        Main.app.processor.reInitializeAll();
                        Main.app.processor.repaintGraphics();
                        prevPortCommand = "COM6";
                        Main.app.runtimeFrame.changeConnIcon(0);
                        try {
                            Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                            Main.app.runtimeFrame.setToolBarButton("Start", false);
                            Main.app.runtimeFrame.setToolBarButton("Stop", true);
                        } catch (Exception ex) {}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if ("com7".equals(command)) {
                if (Main.app != null) {
                    unselectLastPortItem(prevPortCommand);
                    try {
                        ((JMenuItem) e.getSource()).setSelected(true);
                        Main.gotTables=false;Main.presageTable0[0] = 0;
                        SerialPort.port.setSelectedItem("COM7");
                        Main.app.processor.reInitializeAll();
                        Main.app.processor.repaintGraphics();
                        prevPortCommand = "COM7";
                        Main.app.runtimeFrame.changeConnIcon(0);
                        try {
                            Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                            Main.app.runtimeFrame.setToolBarButton("Start", false);
                            Main.app.runtimeFrame.setToolBarButton("Stop", true);
                        } catch (Exception ex) {}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if ("com8".equals(command)) {
                if (Main.app != null) {
                    unselectLastPortItem(prevPortCommand);
                    try {
                        ((JMenuItem) e.getSource()).setSelected(true);
                        Main.gotTables=false;Main.presageTable0[0] = 0;
                        SerialPort.port.setSelectedItem("COM8");
                        Main.app.processor.reInitializeAll();
                        Main.app.processor.repaintGraphics();
                        prevPortCommand = "COM8";
                        Main.app.runtimeFrame.changeConnIcon(0);
                        try {
                            Main.app.runtimeFrame.startMenuItem.setEnabled(false);
                            Main.app.runtimeFrame.setToolBarButton("Start", false);
                            Main.app.runtimeFrame.setToolBarButton("Stop", true);
                        } catch (Exception ex) {}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if ("exit".equals(command)) {
                Main.app.saveAndExit();
                Main.app.systemExit();
                return;
            } else if ("about presage monitor".equals(command)) {
                try {
                    DialogHelpAboutPresage dialog = new DialogHelpAboutPresage(Main.
                            app.
                            runtimeFrame.frame);
                    dialog.locateOnComponent(Main.app.runtimeFrame.frame);
                    ((DialogHelpAboutPresage) dialog).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("help".equals(command)) {
                try {
                    DialogHelpPresageAppHelp dialog = new DialogHelpPresageAppHelp(
                            Main.
                            app.runtimeFrame.frame);
                    dialog.locateOnComponent(Main.app.runtimeFrame.frame);
                    ((DialogHelpPresageAppHelp) dialog).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("presage device help".equals(command)) {
                try {

                    DialogHelpPresageHelp dialog = new DialogHelpPresageHelp(Main.
                            app.
                            runtimeFrame.frame);
                    dialog.locateOnComponent(Main.app.runtimeFrame.frame);
                    ((DialogHelpPresageHelp) dialog).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("display all channels".equals(command)) {
                if (Main.app.displayAllChannels==true){
                    Main.app.displayAllChannels=false;
                }
                else{
                    Main.app.displayAllChannels=true;
                }
            } else if ("degrees f".equals(command)) {
                Main.app.setTempC(false);
                Main.app.runtimeFrame.tempfMenuItem.setSelected(true);
                Main.app.runtimeFrame.tempcMenuItem.setSelected(false);
                Main.app.runtimeFrame.changetempIcon(0); //grab from prefs file later
            } else if ("degrees c".equals(command)) {
                Main.app.setTempC(true);
                Main.app.runtimeFrame.tempcMenuItem.setSelected(true);
                Main.app.runtimeFrame.tempfMenuItem.setSelected(false);
                Main.app.runtimeFrame.changetempIcon(1); //grab from prefs file later
            } else if ("clear".equals(command)) {
                if (Main.app != null) {
                    if (Main.app.processor.isProcessing()) {
                        Main.app.processor.stopProcessing();
                    }
                    try {
                        Main.app.processor.repaintGraphics();
                    } catch (Exception E) {
                        E.printStackTrace();
                        System.out.println("printstack repaint");
                    }

                    try {
                        Main.app.processor.initializeAll();
                    } catch (Exception E) {
                        E.printStackTrace();
                        System.out.println("printstack initall");
                    }

                }
            } else if ("session manager".equals(command)) {


                Main.app.livemode=false;
                try{
                    //Main.app.impl = SerialPortFactory.getInstance("javax.comm");
                    //SerialMain.impl.close();

                    //Main.app.dispose();
                    Main.app.newIsTool = false;
                    filemngrdesign = new File(Main.app.getDesignFolder(), "sessionmanager.bpd");
                    Main.app.newDesign = filemngrdesign;

                    //Main.app.sessionkeyframe.setVisible(true);
                    //int yy =   ( Main.app.runtimeFrame.frame.getY() - Main.app.runtimeFrame.frame.getHeight() );
                    //Main.app.sessionkeyframe.setBounds(Main.app.runtimeFrame.frame.getX(), Main.app.runtimeFrame.frame.getY(), 850, 115);



/*
                    Main.app.dispose();
                    Main.app.newIsTool = false;
                    designName = "sessionmanager.bpd";
                    Main.app.newDesign = Main.app.getDesignFile(designName);
                    //Main.app.run();
  */


                    /*
                    if (Main.app.processor != null)
                        Main.app.processor.dispose();
                    if (Main.app.designFrame != null)
                        Main.app.designFrame.dispose();
                    if (Main.app.runtimeFrame != null)
                        Main.app.runtimeFrame.dispose();


                                  Main.app.runtimeFrame.livemode=false;
                    */
                    /*
                    Main.app.newIsTool = false;
                    Main.app.newDesign = filemngrdesign;
                    */
                    //Main.app.mainStart(args);

                    /*
                    if (Main.app.processor != null)
                        Main.app.processor.dispose();
                    if (Main.app.designFrame != null)
                        Main.app.designFrame.dispose();
                    if (Main.app.runtimeFrame != null)
                    Main.app.runtimeFrame.dispose();
                    */


                  }
                catch(Exception ex){}


                //Main.app.newDesign = Main.app.getDesignFile(filemngrdesign);


                //Main.app.runtimeFrame.changetempIcon(1); //grab from prefs file later
            } else if ("record session".equals(command)) {
                Main.app.livemode=true;
                try{
                    //Main.app.impl = SerialPortFactory.getInstance("javax.comm");
                    //SerialMain.impl.close();
                    //Main.app.dispose();
                    Main.app.newIsTool = false;
                    filemngrdesign = new File(Main.app.getDesignFolder(), "default.bpd");
                    Main.app.newDesign = filemngrdesign;


                    Main.app.sessionkeyframe.setVisible(false);
                    /*
                                         designName = "default.bpd";
                    Main.app.dispose();
                    args[0] = "default.bpd";
                    Main.main(args);
*/

                    /*
                    if (Main.app.processor != null)
                        Main.app.processor.dispose();
                    if (Main.app.designFrame != null)
                        Main.app.designFrame.dispose();
                    if (Main.app.runtimeFrame != null)
                        Main.app.runtimeFrame.dispose();

                filemngrdesign = new File(Main.app.getDesignFolder(), "default.bpd");
                                  Main.app.runtimeFrame.livemode=true;*/
                  }
                catch(Exception ex){}

                //Main.app.newIsTool = false;
                //Main.app.newDesign = Main.app.getDesignFile(filemngrdesign);

                //Main.app.newDesign = filemngrdesign;
                //Main.app.runtimeFrame.changetempIcon(1); //grab from prefs file later
            }


        }


	/**
	 * Invoked when the component has been made invisible.
	 */
public void componentHidden(java.awt.event.ComponentEvent e) {}
	/**
	 * Invoked when the component's position changes.
	 */
public void componentMoved(java.awt.event.ComponentEvent e) {
	Main.app.runtimeFrame.moveChilds();
}
	/**
	 * Invoked when the component's size changes.
	 */
public void componentResized(java.awt.event.ComponentEvent e) {}
	/**
	 * Invoked when the component has been made visible.
	 */
public void componentShown(java.awt.event.ComponentEvent e) {}
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
public void reinit() {
	try {
	} catch (Exception e) {
		e.printStackTrace();
	}
}
public static void setDebug(boolean newValue) {
	debug = newValue;
}
	/**
	 * Invoked when the window is set to be the user's
	 * active window, which means the window (or one of its
	 * subcomponents) will receive keyboard events.
	 */
public void windowActivated(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window has been closed as the result
	 * of calling dispose on the window.
	 */
public void windowClosed(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when the user attempts to close the window
	 * from the window's system menu.  If the program does not
	 * explicitly hide or dispose the window while processing
	 * this event, the window close operation will be cancelled.
	 */
public void windowClosing(java.awt.event.WindowEvent e) {
	((Window) e.getSource()).dispose();
}
	/**
	 * Invoked when a window is no longer the user's active
	 * window, which means that keyboard events will no longer
	 * be delivered to the window or its subcomponents.
	 */
public void windowDeactivated(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window is changed from a minimized
	 * to a normal state.
	 */
public void windowDeiconified(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked when a window is changed from a normal to a
	 * minimized state. For many platforms, a minimized window
	 * is displayed as the icon specified in the window's
	 * iconImage property.
	 * @see Frame#setIconImage
	 */
public void windowIconified(java.awt.event.WindowEvent e) {}
	/**
	 * Invoked the first time a window is made visible.
	 */
public void windowOpened(java.awt.event.WindowEvent e) {}

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
