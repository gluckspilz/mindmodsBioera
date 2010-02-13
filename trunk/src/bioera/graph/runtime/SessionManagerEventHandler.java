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
import java.io.File;
import bioera.serial.*;
import bioera.Log;
import java.io.FileInputStream;
import bioera.serial.SerialPortFactory;


/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */
public class SessionManagerEventHandler implements MouseListener, MouseMotionListener, ActionListener, WindowListener, ComponentListener, KeyListener {


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
public SessionManagerEventHandler(SessionManagerFrame f, GraphPanel p) {
        super();
        Main.app.sessionmanagerFrame = f;
        panel2 = p;
}
public SessionManagerEventHandler(SessionManagerFrame f, JGraphPanel p) {
    super();
    Main.app.sessionmanagerFrame = f;
    panel = p;
}
        /**
         * Invoked when an action occurs.
         */

private static String getCurrentPort(){
            String str = new String(SerialPort.port.getSelectedItem());
            return str.toLowerCase();
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            String command = e.getActionCommand().toLowerCase();
            String command2 = e.paramString();
            if (prevPortCommand == null) {
                prevPortCommand = getCurrentPort();
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

            if ("re-initialize".equals(command)) {

                if (Main.app != null) {
                    if (Main.app.processor.isProcessing()) {

                        Main.app.processor.stopProcessing();
                    }
                    try {
                        Main.app.processor.repaintGraphics();
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                    ;
                    try {
                        Main.app.processor.initializeAll();
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                    ;
                }
            } else if ("open".equals(command)) {
                if (Main.app != null) {
                    try {

                    javax.swing.JFileChooser d = new javax.swing.JFileChooser(Main.app.getSessionsFolder());

                    d.setCurrentDirectory(Main.app.getSessionsFolder());
                    d.setDialogTitle("Open Session");
                    d.addChoosableFileFilter(new ExtFileFilter("PreSage Dual Mode Sessions", "se2"));
                    d.addChoosableFileFilter(new ExtFileFilter("PreSage Single Mode Sessions", "se1"));


                    d.setCurrentDirectory(Main.app.getSessionsFolder());
                    //d.setAcceptAllFileFilterUsed(false);

                    //d.setFileSelectionMode(d.FILES_ONLY); //keep?
                    d.setMultiSelectionEnabled(true);

                    int ret = d.showOpenDialog(Main.app.sessionmanagerFrame.frame);
                    if (ret == d.APPROVE_OPTION) {
                        java.io.File[] f = d.getSelectedFiles();

                        Main.app.sessionmanagerFrame.loadSession(f);

                    }



                    }
                    catch (Exception E) {
                        E.printStackTrace();
                    }
                }

            } else if ("print".equals(command)) {
            } else if ("table editor".equals(command)) {
                try {
                    //Main.app.runtimeFrame.tableEditor();
                    Main.app.runtimeFrame.frame.setVisible(false);
                    Main.app.toggleTableEditor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("exit".equals(command)) {
                //Main.app.saveAndExit();
                Main.app.sessionmanagerFrame.setinVisible();
                return;
            } else if ("about presage monitor".equals(command)) {
                try {
                    DialogHelpAboutPresage dialog = new DialogHelpAboutPresage(Main.
                            app.
                            sessionmanagerFrame.frame);
                    dialog.locateOnComponent(Main.app.sessionmanagerFrame.frame);
                    ((DialogHelpAboutPresage) dialog).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("help".equals(command)) {
                try {
                    DialogHelpPresageAppHelp dialog = new DialogHelpPresageAppHelp(
                            Main.
                            app.sessionmanagerFrame.frame);
                    dialog.locateOnComponent(Main.app.sessionmanagerFrame.frame);
                    ((DialogHelpPresageAppHelp) dialog).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("presage device help".equals(command)) {
                try {

                    DialogHelpPresageHelp dialog = new DialogHelpPresageHelp(Main.
                            app.
                            sessionmanagerFrame.frame);
                    dialog.locateOnComponent(Main.app.sessionmanagerFrame.frame);
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
                    SerialMain.impl.close();

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
                    SerialMain.impl.close();
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
        Main.app.sessionmanagerFrame.moveChilds();
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
