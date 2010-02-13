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
import org.jdesktop.swingx.JXStatusBar;

import java.io.File;
import java.io.FileInputStream;

/**
 * Creation date: (2/25/2004 10:53:54 AM)
 * @author: Jarek Foltynski
 */

public class SessionManagerFrame {


        public JFrame frame;
        private boolean isSwing = true;
        public boolean framedCharts = false;
        public JToolBar toolBar;
        public JMenuBar bar;
        public JGraphPanel panel;
//        public GraphPanel jpanel;
        SessionManagerEventHandler sessionmanagerEventHandler;
        MouseEventHandler mouseEventHandler;

        public static JMenuItem erroritem, saveMenuItem;
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
public SessionManagerFrame() {
        this("Session Manager");

}
/**
 * App constructor comment.
 */
public SessionManagerFrame(String name) {



    frame = new JFrame(name);
    frame.setResizable(false);

}


/**
 * App constructor comment.
 */



public Chart addChart(Chart chart) {

        charts.add(chart);

        //if (framedCharts)
                //layDialogChart(chart);
        //else
                layCanvasChart(chart);
        if (mouseEventHandler != null)
                chart.getComponent().addMouseListener(mouseEventHandler);
        return chart;
}

public JChart addChart(JChart chart) {

            charts.add(chart);

            //if (framedCharts)
                    //layDialogChart(chart);
            //else
                    layCanvasChart(chart);
            if (mouseEventHandler != null)
                    chart.getComponent().addMouseListener(mouseEventHandler);
            return chart;
}
/**
 * App constructor comment.
 */
public void dispose() {
        sessionmanagerEventHandler.disposed = true;
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

        loadSwingSessMngr();


        XmlConfigSection sessionmanagerFrame = xml.getSection("Runtime_frame");
        //designfilecom(xml); //not in designframe
        if (firstrun ==true){

            frame.setBounds(sessionmanagerFrame.getInteger("x", 50),
                                    sessionmanagerFrame.getInteger("y", 50),
                                    sessionmanagerFrame.getInteger("width", 600),
                                sessionmanagerFrame.getInteger("height", 400));

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



public void loadSwingSessMngr() throws Exception {


    //panel.dispose();
    frame.dispose();
    //this.dispose();

    //JFrame frame = (JFrame)this.frame;
    frame.setBackground(ConfigurableSystemSettings.backgroundColor.getAWTColor());
    frame.addWindowListener(new CloseSaveWindowsListener());
    panel = new JGraphPanel(frame);
    sessionmanagerEventHandler = new SessionManagerEventHandler(this, panel);
    panel.addMouseListener(mouseEventHandler);
    frame.addComponentListener(sessionmanagerEventHandler);
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


        Main.app.processor.startProcessing();

}






public int indexOf(String item) {
            //if (name == null)
             //   return defaultIndex;

            for (int i = 0; i < availPortItems.length; i++) {
                if (availPortItems[i].equals(item))
                    return i;
            }

            return -1;
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
            item.addActionListener(sessionmanagerEventHandler);
            m.addSeparator();
            item = new JMenuItem("Exit");
            m.add(item);
            item.addActionListener(sessionmanagerEventHandler);

            //RUNTIME PULLDOWN
            bar.add(m = new JMenu("Sessions"));

            item = new JMenuItem ("Open");
            m.add(item);
            item.addActionListener(sessionmanagerEventHandler);


            //INFO PULLDOWN
            bar.add(m = new JMenu("Help"));

            item= new JMenuItem("Help");
            m.add(item);
            item.addActionListener(sessionmanagerEventHandler);

            item = new JMenuItem("Presage Device Help");
            m.add(item);
            item.addActionListener(sessionmanagerEventHandler);

            item= new JMenuItem("About Presage Monitor");
            m.add(item);
            item.addActionListener(sessionmanagerEventHandler);

            //error trigger (not shown)
            item=new JMenuItem("error");
            erroritem = item;
            item.addActionListener(sessionmanagerEventHandler);

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
                        //layDialogChart(chart);
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
                                             sessionmanagerFrame.frame, errorcode);
        dialog.locateOnComponent(Main.app.sessionmanagerFrame.frame);
        ((DialogError) dialog).show();
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    //Main.app.runtimeFrame.errorDialog(errornum);
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

public void setveryVisible() {
                frame.setVisible(true);
                //frame.show();

}
public void setinVisible() {
            frame.setVisible(false);
            //frame.show();

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

/*
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
*/
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
                                            button.addActionListener(sessionmanagerEventHandler);
                                            button.addMouseListener(sessionmanagerEventHandler);
                                            toolBar.add(button);
                                    }
                            }



                            return toolBar;
}











}
