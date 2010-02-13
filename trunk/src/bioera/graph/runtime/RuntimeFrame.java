/* RuntimeFrame.java v 1.0.9   11/6/04 7:15 PM
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
import bioera.graph.designer.*;
import bioera.graph.chart.*;
import bioera.processing.impl.*;
import bioera.processing.*;
import bioera.config.*;
import bioera.serial.*;
import bioera.processing.impl.SerialPort;

//import de.javasoft.plaf.synthetica.SyntheticaLookAndFeel;
//import de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel;

import org.jdesktop.swingx.JXStatusBar;

import java.io.File;
import java.io.FileInputStream;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */

public class RuntimeFrame {


        public JFrame frame;
        private boolean isSwing = true;
        public boolean framedCharts = false;
        public JToolBar toolBar;
        public JMenuBar bar;
        public JProgressBar progressBar;
        public JProgressBar sensBar;
        public JGraphPanel panel;
//        public GraphPanel jpanel;
        RuntimeEventHandler runtimeEventHandler;
        MouseEventHandler mouseEventHandler;

        private static JLabel templabel;
        private static JLabel temp2label;
        public static JMenuItem startMenuItem, com1MenuItem, com2MenuItem,
                com3MenuItem, com4MenuItem, com5MenuItem, com6MenuItem,
                com7MenuItem, com8MenuItem, stopMenuItem, tempcMenuItem, tempfMenuItem, erroritem, saveMenuItem;
        public static JButton calibrateButton;
        public static JButton sensMeter;
        private Vector charts = new Vector();
        private Vector dialogs = new Vector();
        String[] availPortItems = null;
        protected static boolean debug = bioera.Debugger.get("runtime.frame");
        boolean firstrun = true;
        public JLabel statusLengthl = new JLabel();
        public JTextField statusLengthf = new JTextField();
        public JLabel statusModel = new JLabel();
        public JTextField statusModef = new JTextField();
        public JLabel statusSensl = new JLabel();
        public JTextField statusSensf = new JTextField();
        public JLabel statusProbel = new JLabel();
        public JTextField statusProbef = new JTextField();
        public JLabel statusTimerl = new JLabel();
        public JTextField statusTimerf = new JTextField();



/**
 * App constructor comment.
 */
public RuntimeFrame() {
        this("Runtime");

}
/**
 * App constructor comment.
 */
public RuntimeFrame(String name) {

    frame = new JFrame(name);
    frame.setResizable(false);

}


/**
 * App constructor comment.
 */



public Chart addChart(Chart chart) {

        charts.add(chart);

        if (framedCharts)
                layDialogChart(chart);
        else
                layCanvasChart(chart);
        if (mouseEventHandler != null)
                chart.getComponent().addMouseListener(mouseEventHandler);
        return chart;
}

public JChart addChart(JChart chart) {

            charts.add(chart);

            if (framedCharts)
                    layDialogChart(chart);
            else
                    layCanvasChart(chart);
            if (mouseEventHandler != null)
                    chart.getComponent().addMouseListener(mouseEventHandler);
            return chart;
}
/**
 * App constructor comment.
 */
public void dispose() {
        runtimeEventHandler.disposed = true;
        frame.dispose();


}
/**
 * App constructor comment.
 */
public Frame getFrame() {

        return frame;
}
/**
 * App constructor comment.
 */
public void init(XmlConfigSection xml) throws Exception {


    if (Main.app.livemode == true){

        //if (Main.app.

        loadSwingLive();
    }
    else{


        loadSwingSessMngr();
    }

        XmlConfigSection runtimeFrame = xml.getSection("Runtime_frame");
        //designfilecom(xml); //not in designframe
        if (firstrun ==true){

            frame.setBounds(runtimeFrame.getInteger("x", 50),
                                    runtimeFrame.getInteger("y", 50),
                                    runtimeFrame.getInteger("width", 600),
                                runtimeFrame.getInteger("height", 400));

            firstrun = false;
        }




}

public void designfilecom(XmlConfigSection xml) throws Exception {

    XmlConfigSection ElementSet = xml.getSection("ElementSet");
    java.util.List sectlist = ElementSet.getElementSections();
    sectlist.toString();
/*    for (int i = 0; i < sectlist.size(); i++){

                list.set(i, new XmlConfigSection((XmlConfig) list.get(i)));
    }*/



}



 public void loadSwingLive() throws Exception {
     frame.dispose();
     //JFrame frame = (JFrame)this.frame;
     frame.setBackground(ConfigurableSystemSettings.backgroundColor.getAWTColor());
     frame.addWindowListener(new CloseSaveWindowsListener());
     panel = new JGraphPanel(frame);
     runtimeEventHandler = new RuntimeEventHandler(this, panel);
     panel.addMouseListener(mouseEventHandler);
     frame.addComponentListener(runtimeEventHandler);
     frame.getContentPane().removeAll();
     panel.setLayout(null);
     frame.getContentPane().add(panel, BorderLayout.CENTER);
     panel.setBackground(ConfigurableSystemSettings.panelbgColor.getAWTColor());

     //frame.setBounds(56, 62, 450, 300);
     loadLiveMenus();



     frame.getContentPane().add(loadLiveToolBar(), java.awt.BorderLayout.NORTH);
     frame.getContentPane().add(createStatusBar(), java.awt.BorderLayout.SOUTH);


  }

public void loadSwingSessMngr() throws Exception {


    //panel.dispose();
    frame.dispose();
    //this.dispose();

    //JFrame frame = (JFrame)this.frame;
    frame.setBackground(ConfigurableSystemSettings.backgroundColor.getAWTColor());
    frame.addWindowListener(new CloseSaveWindowsListener());
    panel = new JGraphPanel(frame);
    runtimeEventHandler = new RuntimeEventHandler(this, panel);
    panel.addMouseListener(mouseEventHandler);
    frame.addComponentListener(runtimeEventHandler);
    frame.getContentPane().removeAll();
    panel.setLayout(null);
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    panel.setBackground(ConfigurableSystemSettings.panelbgColor.getAWTColor());
    loadSessMngrMenus();
    frame.getContentPane().add(loadSessMngrToolBar(), java.awt.BorderLayout.NORTH);

    Main.app.sessionkeyframe.setVisible(true);
    Main.app.sessionkeyframe.setBounds(100, 700, 850, 115);


    //frame.getContentPane().add(createStatusBar(), java.awt.BorderLayout.SOUTH);


}


public void loadSession(File[] f) throws Exception {

    int sessFileinLeng = 0;
    Main.app.numberofSessions=0;
    Main.app.fileByteMatrix = Main.app.emptyByteMatrix;
    Main.app.fileByteMatrixSize=Main.app.emptyByteMatrixSize;

    byte sessFileBuffer[] = new byte[24576 * 16];


    if (f[0] != null && f[0].exists() && f[0].isFile()) {
        if ( ((f[0].getName().contains("se1")) && (f.length>12)) || ((f[0].getName().contains("se2")) && (f.length>6)) ){ //throw error if over max
            errorDialog(104);
        }
        else{
            try {
                for (int i = 0; i < f.length+1; i++) {
                    try{
                        FileInputStream sfin = new FileInputStream(f[i]);
                        sessFileinLeng = sfin.read(sessFileBuffer);
                        Main.app.fileByteMatrixSize[i][0] = sessFileinLeng;
                        for (int j =0; j <= sessFileinLeng; j++){
                            Main.app.fileByteMatrix[i][j] = sessFileBuffer[j];
                        }
                        Main.app.numberofSessions++;
                        Main.app.sessionFileDate[i] = f[i].lastModified();
                    }
                    catch(Exception E){}
                }
            } catch (Exception ex) {
                System.out.println("Could not open Session '" + f +
                                   "' : " + ex);
                ex.printStackTrace();
            }

        }
    }
    else{errorDialog(103); //null || !exists || notfile
    }

    bioera.device.impl.Presagedevice.loadSession(); //sets sessionactive&loadsession to true & starts processor

    if (Main.app != null) {
        Main.app.runtimeFrame.startMenuItem.setEnabled(false);
        Main.app.runtimeFrame.stopMenuItem.setEnabled(true);
        Main.app.runtimeFrame.tempcMenuItem.setEnabled(false);
        Main.app.runtimeFrame.tempfMenuItem.setEnabled(false);
        try {
            Main.app.runtimeFrame.setToolBarButton("Start", false);
            Main.app.runtimeFrame.setToolBarButton("Stop", true);
        } catch (Exception ex) {}

        Main.app.processor.startProcessing();
    }
}


private JXStatusBar createStatusBar()
          {
            JXStatusBar statusBar = new JXStatusBar();
            //weight of 1.0 means expand to max available width
            //statusBar.add(new JLabel("Ready"), new JXStatusBar.Constraint(1.0));


            File cbad = new File(Main.app.getImagesFolder(),"connbad.gif");
            File cgood = new File(Main.app.getImagesFolder(),"conngood.gif");
            File fcels = new File(Main.app.getImagesFolder(),"cels.gif");
            File ffahr = new File(Main.app.getImagesFolder(),"fahr.gif");

            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(ffahr.getAbsolutePath()));  //grab from prefs file later
            templabel = label;


            JLabel label2 = new JLabel();
            if (Main.gotTables==false){
                label2.setIcon(new ImageIcon(cbad.getAbsolutePath()));
                try{
                    Main.app.runtimeFrame.setToolBarButton("Start", false);
                    Main.app.runtimeFrame.setToolBarButton("Stop", false);
                    setToolBarButton("Table Editor", false);
                }                catch(Exception Ex){}
            } else {
                try{
                label2.setIcon(new ImageIcon(cgood.getAbsolutePath()));
                Main.app.runtimeFrame.setToolBarButton("Start", true);
                Main.app.runtimeFrame.setToolBarButton("Stop", false);
                    setToolBarButton("Table Editor", true);                }catch(Exception Ex){}
            }
            temp2label = label2;


            //create constraint to add space of 10 at components right side
            JXStatusBar.Constraint ct = new JXStatusBar.Constraint(0.0, new Insets(0, 0, 0, 10));
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);

            statusLengthl.setText("Session Length");
            statusModel.setText("Mode");
            statusSensl.setText("Sensitivity");
            statusProbel.setText("Probe");
            statusTimerl.setText("Timer");

            statusLengthf.setText("   ");
            statusModef.setText("   ");
            statusSensf.setText("   ");
            statusProbef.setText("   ");
            statusTimerf.setText("   ");

            statusLengthf.setPreferredSize(new Dimension(45, 8));
            statusLengthf.setEditable(false);
            statusLengthf.setHorizontalAlignment(JTextField.CENTER);
            statusModef.setPreferredSize(new Dimension(45, 8));
            statusModef.setEditable(false);
            statusModef.setHorizontalAlignment(JTextField.CENTER);
            statusSensf.setPreferredSize(new Dimension(45, 8));
            statusSensf.setEditable(false);
            statusSensf.setHorizontalAlignment(JTextField.CENTER);
            statusProbef.setPreferredSize(new Dimension(45, 8));
            statusProbef.setEditable(false);
            statusProbef.setHorizontalAlignment(JTextField.CENTER);
            statusTimerf.setPreferredSize(new Dimension(45, 8));
            statusTimerf.setEditable(false);
            statusTimerf.setHorizontalAlignment(JTextField.CENTER);


            //statusLengthf.setMinimumSize(new Dimension(45, 20));
            statusBar.add(label2, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(label, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(statusLengthl, ct);
            statusBar.add(statusLengthf, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(statusModel, ct);
            statusBar.add(statusModef, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(statusSensl, ct);
            statusBar.add(statusSensf, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(statusProbel, ct);
            statusBar.add(statusProbef, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(statusTimerl, ct);
            statusBar.add(statusTimerf, ct);




/*
            statusBar.add(new JLabel("Session Length"), ct);
            statusBar.add(statusLength);
            statusLength.add
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(new JLabel("Mode"), ct);
            statusMode.add(statusLength, ct);
            statusBar.add(statusMode, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(new JLabel("Sensitivity"), ct);
            statusBar.add(statusSens, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(new JLabel("Probe Config"), ct);
            statusProbe.add(statusLength, ct);
            statusBar.add(statusProbe, ct);
            statusBar.add(new JSeparator(JSeparator.VERTICAL), ct);
            statusBar.add(new JLabel("Timer:"), ct);
            statusTimer.add(statusLength, ct);
            statusBar.add(statusTimer);*/


            return statusBar;

            //statusLength.setMinimumSize((new Dimension(45, 20)));
            //statusLength.setPreferredSize((new Dimension(45, 5)));

          }

public void changetempIcon(int type){
    if (type == 0){
        File ffahr = new File(Main.app.getImagesFolder(),"fahr.gif");
        templabel.setIcon(new ImageIcon(ffahr.getAbsolutePath()));  //grab from prefs file later
    }
    if (type == 1){
        File fcels = new File(Main.app.getImagesFolder(),"cels.gif");
        templabel.setIcon(new ImageIcon(fcels.getAbsolutePath()));  //grab from prefs file later
    }

}

public void changeConnIcon(int type){

    if (type == 0){
        File cbad = new File(Main.app.getImagesFolder(),"connbad.gif");
        temp2label.setIcon(new ImageIcon(cbad.getAbsolutePath()));
    }
    if (type == 1){
        File cgood = new File(Main.app.getImagesFolder(),"conngood.gif");
        temp2label.setIcon(new ImageIcon(cgood.getAbsolutePath()));
    }

}


private static String getCurrentPort(){
            String str = new String(SerialPort.port.getSelectedItem());
            return str.toLowerCase();
        }

public int indexOf(String item) {
            //if (name == null)
             //   return defaultIndex;

            //for (int i = 0; i < SerialMain.portsavail.length; i++) {
            for (int i = 0; i < SerialMain.impl.ports.length; i++) {
                if (SerialMain.impl.ports[i].equals(item))
                    return i;
            }

            return -1;
}

private void loadLiveMenus() throws Exception {
        bar = new JMenuBar();
        frame.setJMenuBar(bar);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        //SYSTEM PULLDOWN
        JMenu m = new JMenu("System");
        bar.add(m);
        JMenuItem item = new JMenuItem("start");
        m.add(item);
        item.setEnabled(false);
        startMenuItem = item;
        item.addActionListener(runtimeEventHandler);
        item = new JMenuItem("stop");
        m.add(item);
        stopMenuItem = item;
        item.addActionListener(runtimeEventHandler);
        item.setEnabled(false);

        m.addSeparator();

        item = new JMenuItem("exit");
        m.add(item);
        item.addActionListener(runtimeEventHandler);

        //RUNTIME PULLDOWN
        bar.add(m = new JMenu("Sessions"));


        item = new JMenuItem ("Save Session");
        item.setEnabled(false);
        m.add(item);
        saveMenuItem = item;
        item.addActionListener(runtimeEventHandler);


        //CONFIGURATION PULLDOWN
        bar.add(m = new JMenu("Configuration"));
        JMenu temp = null;
        temp = (JMenu) m.add(new JMenu("Temperature"));
        JRadioButtonMenuItem mi = (JRadioButtonMenuItem) temp.add(new JRadioButtonMenuItem("Degrees F"));
        mi.setSelected(true);
        tempfMenuItem = mi;
        mi.addActionListener(runtimeEventHandler);
        mi = (JRadioButtonMenuItem) temp.add(new JRadioButtonMenuItem("Degrees C"));
        tempcMenuItem = mi;
        mi.setSelected(false);
        mi.addActionListener(runtimeEventHandler);
        item = new JCheckBoxMenuItem("Display All Channels");
        item.setSelected(false);
        m.add(item);
        item.addActionListener(runtimeEventHandler);


        if (!Main.app.osName.startsWith("Windows")) {
        //if (Main.app.osName.startsWith("Windows")) {
            //COM PORT PULLDOWN
            bar.add(m = new JMenu("COM Port"));

            item = new JMenuItem("Re-Initialize");
            m.add(item);
            item.addActionListener(runtimeEventHandler);
            m.addSeparator();

            if (indexOf("COM1") != -1) {
                item = new JRadioButtonMenuItem("COM1", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com1MenuItem = item;
            } else {
                item = new JRadioButtonMenuItem("COM1", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com1MenuItem = item;
                item.setEnabled(false);
            }

            if (indexOf("COM2") != -1) {
                item = new JRadioButtonMenuItem("COM2");
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com2MenuItem = item;
            } else {
                item = new JRadioButtonMenuItem("COM2", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com2MenuItem = item;
                item.setEnabled(false);
            }
            if (indexOf("COM3") != -1) {
                item = new JRadioButtonMenuItem("COM3");
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com3MenuItem = item;
            } else {
                item = new JRadioButtonMenuItem("COM3", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com3MenuItem = item;
                item.setEnabled(false);
            }

            if (indexOf("COM4") != -1) {
                item = new JRadioButtonMenuItem("COM4");
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com4MenuItem = item;
            } else {
                item = new JRadioButtonMenuItem("COM4", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com4MenuItem = item;
                item.setEnabled(false);
            }

            if (indexOf("COM5") != -1) {
                item = new JRadioButtonMenuItem("COM5");
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com5MenuItem = item;
            } else {
                item = new JRadioButtonMenuItem("COM5", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com5MenuItem = item;
                item.setEnabled(false);
            }

            if (indexOf("COM6") != -1) {
                item = new JRadioButtonMenuItem("COM6");
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com6MenuItem = item;
            } else {
                item = new JRadioButtonMenuItem("COM6", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com6MenuItem = item;
                item.setEnabled(false);
            }

            if (indexOf("COM7") != -1) {
                item = new JRadioButtonMenuItem("COM7");
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com7MenuItem = item;
            } else {
                item = new JRadioButtonMenuItem("COM7", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com7MenuItem = item;
                item.setEnabled(false);
            }

            if (indexOf("COM8") != -1) {
                item = new JRadioButtonMenuItem("COM8");
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com8MenuItem = item;
            } else {
                item = new JRadioButtonMenuItem("COM8", false);
                m.add(item);
                item.addActionListener(runtimeEventHandler);
                com8MenuItem = item;
                item.setEnabled(false);
            }
        }




        //INFO PULLDOWN
        bar.add(m = new JMenu("Help"));

        item= new JMenuItem("Help");
        m.add(item);
        item.addActionListener(runtimeEventHandler);

        item = new JMenuItem("Presage Device Help");
        m.add(item);
        item.addActionListener(runtimeEventHandler);

        item= new JMenuItem("About Presage Monitor");
        m.add(item);
        item.addActionListener(runtimeEventHandler);

        //error trigger (not shown)
        item=new JMenuItem("error");
        erroritem = item;
        item.addActionListener(runtimeEventHandler);



}



private void loadSessMngrMenus() throws Exception {
            bar = new JMenuBar();
            frame.setJMenuBar(bar);
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);

            //SYSTEM PULLDOWN
            JMenu m = new JMenu("System");
            bar.add(m);


            JMenuItem item = new JMenuItem("Print");
            m.add(item);
            item.addActionListener(runtimeEventHandler);
            m.addSeparator();
            item = new JMenuItem("Exit");
            m.add(item);
            item.addActionListener(runtimeEventHandler);

            //RUNTIME PULLDOWN
            bar.add(m = new JMenu("Sessions"));

            item = new JMenuItem ("Open");
            m.add(item);
            item.addActionListener(runtimeEventHandler);


            //INFO PULLDOWN
            bar.add(m = new JMenu("Help"));

            item= new JMenuItem("Help");
            m.add(item);
            item.addActionListener(runtimeEventHandler);

            item = new JMenuItem("Presage Device Help");
            m.add(item);
            item.addActionListener(runtimeEventHandler);

            item= new JMenuItem("About Presage Monitor");
            m.add(item);
            item.addActionListener(runtimeEventHandler);

            //error trigger (not shown)
            item=new JMenuItem("error");
            erroritem = item;
            item.addActionListener(runtimeEventHandler);

}

/**
 * App constructor comment.
 */

public static void main(String args[]) throws Exception {
        try {

                System.out.println("started");
                //new RuntimeFrame();
                //RuntimeFrame.runtimeApp.addWindowListener(new CloseWindowsListener());
                //RuntimeFrame.runtimeApp.add(new HorizontalLinearChart());
                //RuntimeFrame.runtimeApp.add(new HorizontalLinearChart());
                //RuntimeFrame.runtimeApp.show();
                System.out.println("Finished");
        } catch (Exception e) {
                e.printStackTrace();
        }
}
/**
 * App constructor comment.
 */
public void moveChilds() {
        ChartDialog d;
        for (int i = 0; i < dialogs.size(); i++){
                d = (ChartDialog) dialogs.elementAt(i);
                d.setLocation(frame.getX() + panel.getX() + d.relativeX,
                        frame.getY() + panel.getY() + d.relativeY);
        }
}
/**
 * App constructor comment.
 */
public void reload() throws Exception {
        if (framedCharts) {
                dialogs.removeAllElements();

                // Create dialogs
                panel.removeAll();
                for (int i = 0; i < charts.size(); i++){
                        Chart chart = (Chart) charts.elementAt(i);
                        layDialogChart(chart);
                }
        } else {
                // Move from dialogs to canvas
                ChartDialog d;
                for (int i = 0; i < dialogs.size(); i++){
                        d = (ChartDialog) dialogs.elementAt(i);
                        Dimension size = d.getSize();
                        Chart c = d.chart;
                        c.getComponent().setLocation(d.relativeX, d.relativeY);
                        c.getComponent().setSize(size);
                        d.dispose();
                        layCanvasChart(c);
                }

                dialogs.removeAllElements();
        }
        setVisible();
        //show();
}




public void errorDialog(int errorcode){


    try {
        DialogError dialog = new DialogError(Main.
                                             app.
                                             runtimeFrame.frame, errorcode);
        dialog.locateOnComponent(Main.app.runtimeFrame.frame);
        ((DialogError) dialog).show();
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    progressBar.setVisible(false);
    calibrateButton.setVisible(false);
    sensMeter.setVisible(false);
    //Main.app.runtimeFrame.errorDialog(errornum);
    stopMenuItem.doClick();
    //Main.app.processor.stopProcessing();


}
/**
 * App constructor comment.
 */


public boolean removeChart(Chart c) {
        for (int i = 0; i < charts.size(); i++){
                if (charts.elementAt(i) == c) {
                        charts.removeElementAt(i);
                        if (framedCharts) {
                                for (int j = 0; j < dialogs.size(); j++){
                                        if (c == ((ChartDialog) dialogs.elementAt(j)).chart) {
                                                ((ChartDialog) dialogs.elementAt(j)).dispose();
                                                dialogs.removeElementAt(j);
                                                break;
                                        }
                                }
                        } else {
                                panel.remove(c.getComponent());
                        }

                        return true;
                }
        }

        return false;
}
public boolean removeChart(JChart c) {
    for (int i = 0; i < charts.size(); i++){
        if (charts.elementAt(i) == c) {
            charts.removeElementAt(i);
            if (framedCharts) {
                for (int j = 0; j < dialogs.size(); j++){
                    if (c == ((JChartDialog) dialogs.elementAt(j)).chart) {
                        ((JChartDialog) dialogs.elementAt(j)).dispose();
                        dialogs.removeElementAt(j);
                        break;
                    }
                }
            } else {
                panel.remove(c.getComponent());
            }

            return true;
        }
    }

    return false;
}
/**
 * App constructor comment.
 */
public void repaint() {
        frame.repaint();
        panel.repaint();
        for (int i = 0; i < charts.size(); i++){
                ((Chart) charts.elementAt(i)).getComponent().repaint();
        }


}
/**
 * App constructor comment.
 */
public void save(XmlCreator xml) throws Exception {
        XmlCreator runtimeXml = xml.addSection("Runtime_frame");
        runtimeXml.addTextValue("x", "" + frame.getX());
        runtimeXml.addTextValue("y", "" + frame.getY());
        runtimeXml.addTextValue("width", "" + frame.getWidth());
        runtimeXml.addTextValue("height", "" + frame.getHeight());
}
public static void setDebug(boolean newValue) {
        debug = newValue;
}
/**
 * App constructor comment.
 */
public void setRuntimeTitle(String mode) throws Exception {
        frame.setTitle("PreSage Monitor: " + mode);
}
/**
 * App constructor comment.
 */
//public void show() {
public void setVisible() {
        frame.setVisible(true);
        //frame.show();
        ChartDialog d;
        for (int i = dialogs.size() - 1; i >= 0 ; i--){
                d = (ChartDialog) dialogs.elementAt(i);
                d.setVisible(true);
                //d.show();
                //System.out.println("showing " + d.getTitle());
        }
}



public void inVisible(Chart c) {
         System.out.println(c.getChartName());
        //Component com = c.getComponent();
        //System.out.println(com.getName());
        //int layer = c.getLayer();
        //panel.remove(com);
}

/**
 * App constructor comment.
 */
public void layCanvasChart(Chart chart) {
        Component c = chart.getComponent();
        int layer = chart.getLayer();
        // Insert at the right deepth
        int n = panel.getComponentCount();
        boolean added = false;
        for (int i = 0; i < n; i++){
                ChartComponent cp = (ChartComponent) panel.getComponent(i);
                // The higher layers on the beginning
                if (layer > cp.getChart().getLayer()) {
                        panel.add(c, i);
                        added = true;
                        break;
                }
        }

        if (!added) {
                // Add at the end
                panel.add(c);
        }
}

    public void layCanvasChart(JChart chart) {
            Component c = chart.getComponent();
            int layer = chart.getLayer();
            // Insert at the right deepth
            int n = panel.getComponentCount();
            boolean added = false;
            for (int i = 0; i < n; i++){
                    ChartComponent cp = (ChartComponent) panel.getComponent(i);
                    // The higher layers on the beginning
                    if (layer > cp.getChart().getLayer()) {
                            panel.add(c, i);
                            added = true;
                            break;
                    }
            }

            if (!added) {
                    // Add at the end
                    panel.add(c);
            }




}

/**
 * App constructor comment.
 */
public void layDialogChart(Chart chart) {
        int layer = chart.getLayer();
        //System.out.println("layer=" + layer);
        // Insert at the right deepth by the layout
        int n = dialogs.size();
        boolean added = false;
        for (int i = 0; i < n; i++){
                ChartDialog c = (ChartDialog) dialogs.get(i);
                // The higher layers on the beginning
                if (layer > c.chart.getLayer()) {
                        ChartDialog d = new ChartDialog(this, chart);
                        d.relativeX = chart.getComponent().getX();
                        d.relativeY = chart.getComponent().getY();
                        dialogs.add(i, d);
                        //System.out.println("adding dialog "+d.getTitle()+"at " + i);
                        added = true;
                        break;
                }
        }

        if (!added) {
                ChartDialog d = new ChartDialog(this, chart);
                d.relativeX = chart.getComponent().getX();
                d.relativeY = chart.getComponent().getY();
                dialogs.add(d);
                //System.out.println("adding dialog "+d.getTitle()+"at the end");
        }
}

    public void layDialogChart(JChart chart) {
            int layer = chart.getLayer();
            //System.out.println("layer=" + layer);
            // Insert at the right deepth by the layout
            int n = dialogs.size();
            boolean added = false;
            for (int i = 0; i < n; i++){
                    JChartDialog c = (JChartDialog) dialogs.get(i);
                    // The higher layers on the beginning
                    if (layer > c.chart.getLayer()) {
                            JChartDialog d = new JChartDialog(this, chart);
                            d.relativeX = chart.getComponent().getX();
                            d.relativeY = chart.getComponent().getY();
                            dialogs.add(i, d);
                            //System.out.println("adding dialog "+d.getTitle()+"at " + i);
                            added = true;
                            break;
                    }
            }

            if (!added) {
                    JChartDialog d = new JChartDialog(this, chart);
                    d.relativeX = chart.getComponent().getX();
                    d.relativeY = chart.getComponent().getY();
                    dialogs.add(d);
                    //System.out.println("adding dialog "+d.getTitle()+"at the end");
            }
}

    public static final JMenuItem findMenu(String name, JMenu inside) throws Exception {
            //System.out.println("searching in " + inside.getText() + "  " + inside.getMenuComponentCount());
            if (inside == null || name == null)
                    return null;
            for (int i = 0; i < inside.getMenuComponentCount(); i++){
                    Component c = inside.getMenuComponent(i);
                    if (c != null && c instanceof JMenuItem) {
                            JMenuItem m = (JMenuItem) c;
                            //System.out.println("jmenu=" + m.getText());
                            if (m.getText().equals(name)) {
                                    return m;
                            }
                    }
            }

            return null;
    }
    /**
     * Display constructor comment.
     */
    public static final JMenu findMenu(String name, JMenuBar bar) throws Exception {
            //System.out.println("searching in bar " + bar.getName());
            if (bar == null || name == null)
                    return null;
            for (int i = 0; i < bar.getMenuCount(); i++){
                    JMenu m = bar.getMenu(i);
                    //System.out.println("bar jmenu="  + m.getText());
                    if (m != null && m.getText() != null && m.getText().equals(name)) {
                            return m;
                    }
            }

            return null;
    }
    /**
     * Display constructor comment.
     */
    public JMenuItem getMenuItem(String s) {
            return getMenuItem(s, null);
    }
    /**
     * Display constructor comment.
     */
    public JMenuItem getMenuItem(String s, String s1) {
            //System.out.println("searching for " + s + "  " + s1);
            int n = bar.getMenuCount(), k;
            for (int i = 0; i < n; i++){
                    JMenu m = bar.getMenu(i);
                    //System.out.println("M=" + m.getText());
                    k = m.getItemCount();
                    for (int j = 0; j < k; j++){
                            JMenuItem mi = m.getItem(j);
                            if (mi == null)
                                    continue;
                            //System.out.println("Mi=" + mi.getText());
                            if (s.equals(mi.getText())) {
                                    return mi;
                            } else if (s1 != null && s1.equals(mi.getText())) {
                                    return mi;
                            }
                    }
            }

            return null;
}

//////////////////////////////////////////////////////

/*
    public JButton getToolbarItem(String s, String s1) {
            //System.out.println("searching for " + s + "  " + s1);
            int n = toolBar.getComponentCount(), k;
            for (int i = 0; i < n; i++){
                    Object o = toolBar.getComponent(i);
                    //System.out.println("M=" + m.getText());
                    if (o == null || !(o instanceof JButton))
                            continue;
                    JButton b = (JButton) o;
                    //System.out.println("Mi=" + mi.getText());
                    if (s.equals(b.getText())) {
                            return b;
                    } else if (s1 != null && s1.equals(b.getText())) {
                            return b;
                    }
            }

            return null;
}

 */
        public void setToolBarButton(String name, boolean state) throws Exception {
            int n = toolBar.getComponentCount();
            for (int i = 0; i < n; i++) {
                Component c = toolBar.getComponent(i);
                //System.out.println("comp=" + c.getName());
                if (c instanceof JButton && c.getName().equals(name)) {
                    JButton b = (JButton) c;
                    b.setEnabled(state);
                    //System.out.println("disabled " + name + " " + state);
                    break;
                }
            }
        }



private JToolBar loadLiveToolBar() throws Exception {
            final String toolBarNames[][] = {
                    {"Start", "Start Sesssion", "on"},
                    {"",""},
                    {"Stop", "Stop Session", "off"},
                    {"",""},
                    {"Re-Initialize", "Reset Connection and Display", "on"},
                    {"",""},
                    {"Session Manager", "Session Manager", "on"},
                    {"",""},
                    {"Table Editor", "Table Editor", "on"},
            };

            toolBar = new JToolBar();

            toolBar.setBackground(frame.getBackground());
            toolBar.setForeground(frame.getForeground());
            toolBar.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLUE));
            toolBar.setBorderPainted(true);
            toolBar.setFloatable(false);

            for (int i = 0; i < toolBarNames.length; i++){
                    if (toolBarNames[i][0].length() == 0) {
                            toolBar.addSeparator();
                    } else {
                            JButton button = new JButton();
                            button.setName(toolBarNames[i][0]);
                            button.setText(toolBarNames[i][0]);
                            File f = new File(Main.app.getImagesFolder(), toolBarNames[i][0]);
                            button.setIcon(new ImageIcon(f.getAbsolutePath() + ".gif"));
                            button.setDisabledIcon(new ImageIcon(f.getAbsolutePath() + "_disabled.gif"));
                            button.setMargin(new Insets(0, 0, 0, 0));
                            button.setToolTipText(toolBarNames[i][1]);
                            if ("off".equals(toolBarNames[i][2]))
                                    button.setEnabled(false);
                            button.addActionListener(runtimeEventHandler);
                            button.addMouseListener(runtimeEventHandler);
                            toolBar.add(button);
                    }
            }
            /*
            if(Main.gotTables==false){
                Main.app.runtimeFrame.setToolBarButton("Start", false);
            }*/







            //add sensmeter
            JButton sensbutton = new JButton();
            sensbutton.setText(" Sens Value Here ");
            sensbutton.setMargin(new Insets(0,100,0,0));
            sensbutton.setForeground(Color.blue);
            sensbutton.setVisible(false);
            sensMeter = sensbutton;
            sensBar = new JProgressBar(0, 7);
            sensBar.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
            sensBar.setValue(0);
            sensBar.setStringPainted(true);
            sensBar.setVisible(false);
            toolBar.add(sensbutton);
            toolBar.add(sensBar);




            //add calibrating button/graphic
            JButton button = new JButton();
            button.setText("Calibrating Sensors");
            button.setName("Calibrating Sensors");
            button.setToolTipText("Calibrating Sensors");
            //File f = new File(Main.app.getImagesFolder(), "Calibrating");
            //button.setIcon(new ImageIcon(f.getAbsolutePath() + ".gif"));
            //button.setDisabledIcon(new ImageIcon(f.getAbsolutePath() + "_disabled.gif"));
            button.setMargin(new Insets(0,100,0,0));
            button.setBackground(Color.red);
            button.addActionListener(runtimeEventHandler);
            button.addMouseListener(runtimeEventHandler);
            button.setForeground(Color.red);
            button.setEnabled(false);
            button.setVisible(false);
            toolBar.add(button);
            calibrateButton = button;
            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setVisible(false);
            toolBar.add(progressBar);
            return toolBar;
}


private JToolBar loadSessMngrToolBar() throws Exception {
                            final String toolBarNames[][] = {
                                    {"Open", "Open", "on"},
                                    {"",""},
                                    {"Print", "Print Chart", "on"},
                                    {"",""},
                                    {"Clear", "Clear Chart", "on"},
                                    {"",""},
                                    {"Record Session", "Record a New Session", "on"},
                            };

                            toolBar = new JToolBar();

                            toolBar.setBackground(frame.getBackground());
                            toolBar.setForeground(frame.getForeground());
                            toolBar.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLUE));
                            toolBar.setBorderPainted(true);
                            toolBar.setFloatable(false);

                            for (int i = 0; i < toolBarNames.length; i++){
                                    if (toolBarNames[i][0].length() == 0) {
                                            toolBar.addSeparator();
                                    } else {
                                            JButton button = new JButton();
                                            button.setName(toolBarNames[i][0]);
                                            button.setText(toolBarNames[i][0]);
                                            File f = new File(Main.app.getImagesFolder(), toolBarNames[i][0]);
                                            button.setIcon(new ImageIcon(f.getAbsolutePath() + ".gif"));
                                            button.setDisabledIcon(new ImageIcon(f.getAbsolutePath() + "_disabled.gif"));
                                            button.setMargin(new Insets(0, 0, 0, 0));
                                            button.setToolTipText(toolBarNames[i][1]);
                                            if ("off".equals(toolBarNames[i][2]))
                                                    button.setEnabled(false);
                                            button.addActionListener(runtimeEventHandler);
                                            button.addMouseListener(runtimeEventHandler);
                                            toolBar.add(button);
                                    }
                            }
                            //add calibrating button/graphic
                            JButton button = new JButton();
                            button.setText("Calibrating Sensors");
                            button.setName("Calibrating Sensors");
                            button.setToolTipText("Calibrating Sensors");
                            //File f = new File(Main.app.getImagesFolder(), "Calibrating");
                            //button.setIcon(new ImageIcon(f.getAbsolutePath() + ".gif"));
                            //button.setDisabledIcon(new ImageIcon(f.getAbsolutePath() + "_disabled.gif"));
                            button.setMargin(new Insets(0,300,0,0));
                            button.setBackground(Color.red);
                            button.addActionListener(runtimeEventHandler);
                            button.addMouseListener(runtimeEventHandler);
                            button.setForeground(Color.red);
                            button.setEnabled(false);
                            button.setVisible(false);
                            toolBar.add(button);
                            calibrateButton = button;
                            progressBar = new JProgressBar(0, 100);
                            progressBar.setValue(0);
                            progressBar.setStringPainted(true);
                            progressBar.setVisible(false);
                            toolBar.add(progressBar);
                            return toolBar;
}



public void calibrationIndicator() {

    if (Main.app.calibrating == false) {
        progressBar.setEnabled(true);
        progressBar.setVisible(true);
        calibrateButton.setVisible(true);
        calibrateButton.setEnabled(true);
        Main.app.calibrating = true;
        progressBar.setValue(100 / 8);
    } else {

        //calibratebutton each sec
        if (!calibrateButton.isEnabled()) {
            calibrateButton.setEnabled(true);
        } else {
            calibrateButton.setEnabled(false);
        }

        progressBar.setValue(progressBar.getValue() + (100 / 8));
        if (progressBar.getValue() > 98) {
            calibrateButton.setForeground(Color.GREEN.darker());
            calibrateButton.setText("Calibration Complete!");
            calibrateButton.setEnabled(true);
            Main.app.calibrating = false;
        }
    }
}

public void doSensMeter(int mode) {
    //increase one level
    sensBar.setValue(sensBar.getValue()+1);
    sensMeter.setText(String.valueOf("Sensitivity: "+Main.app.sensValues[mode][sensBar.getValue()-1])+"%");
    sensBar.setString("Level "+sensBar.getValue());
}


public void doSensMeter(int mode, int index) {
    Main.app.runtimeFrame.progressBar.setVisible(false);
    Main.app.runtimeFrame.calibrateButton.setVisible(false);
    sensBar.setEnabled(true);
    sensBar.setVisible(true);
    sensMeter.setVisible(true);
    sensMeter.setEnabled(true);
    sensMeter.setText(String.valueOf("Sensitivity: "+Main.app.sensValues[mode][index])+"%");
    sensBar.setString("Level "+sensBar.getValue());
    sensBar.setValue(index);
}



public void resetAtStop(){
    //reset all menu items for stop here
    saveMenuItem.setEnabled(true);
    startMenuItem.setEnabled(true);
    stopMenuItem.setEnabled(false);
    progressBar.setVisible(false);
    calibrateButton.setVisible(false);
    sensBar.setVisible(false);
    sensMeter.setVisible(false);

}

public void atEOS(int startSens, int endSens, int indTrav, int[] highest, int[] lowest, int[] avg, int probeConf) {
    //reset all menu items for end of session here

    Main.app.processor.stopProcessing();
    if (Main.app.completedfilename.exists()) {
        Main.app.completedfilename.delete();
    } //it will exist if last session completed but not saved
    Main.app.currentfilename.renameTo(Main.app.completedfilename); //rename current to completed
    try {
        setToolBarButton("Start", true);
        setToolBarButton("Stop", false);
        setToolBarButton("Session Manager", true);
        setToolBarButton("Table Editor", true);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    saveMenuItem.setEnabled(true);
    startMenuItem.setEnabled(true);
    stopMenuItem.setEnabled(false);
    progressBar.setVisible(false);
    calibrateButton.setVisible(false);


    if(probeConf ==3 || probeConf ==12){ //if dual chan
        try {
            DialogEOS3 dialog = new DialogEOS3(Main.
                                             app.
                                             runtimeFrame.frame, startSens, endSens,
                                             indTrav, highest, lowest, avg,
                                             probeConf);
            dialog.locateOnComponent(Main.app.runtimeFrame.frame);
            ((DialogEOS3) dialog).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    else{ //if sing chan
        try {
            DialogEOS dialog = new DialogEOS(Main.
                                             app.
                                             runtimeFrame.frame, startSens,
                                             endSens, indTrav, highest, lowest,
                                             avg, probeConf);
            dialog.locateOnComponent(Main.app.runtimeFrame.frame);
            ((DialogEOS) dialog).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //saveMenuItem.doClick();

}





}
